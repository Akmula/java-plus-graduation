package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import ru.practicum.entity.EventSimilarity;
import ru.practicum.entity.UserAction;
import ru.practicum.grpc.stats.message.RecommendedEventProto;
import ru.practicum.repository.EventSimilarityRepository;
import ru.practicum.repository.UserActionRepository;
import ru.practicum.service.RecommendationService;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    private final UserActionRepository userActionRepository;
    private final EventSimilarityRepository similarityRepository;

    @Override
    public List<RecommendedEventProto> getSimilarEvents(long eventId, long userId, int maxResults) {
        log.info("RecommendationService - Получение похожих событий для eventId = {} и userId = {}", eventId, userId);

        List<EventSimilarity> similarities = similarityRepository.findByEvent(eventId);

        List<Long> interactedEvents = userActionRepository.findEventIdsByUserId(userId);

        return similarities.stream()
                .filter(sim -> !interactedEvents.contains(sim.getOtherEventId(eventId)))
                .sorted(Comparator.comparingDouble(EventSimilarity::getScore).reversed())
                .limit(maxResults)
                .map(sim -> RecommendedEventProto.newBuilder()
                        .setEventId(sim.getOtherEventId(eventId))
                        .setScore(sim.getScore())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<RecommendedEventProto> getRecommendationsForUser(long userId, int maxResults) {
        log.info("RecommendationService - Получение рекомендаций для userId={}", userId);

        Pageable recentPage = PageRequest.of(0, 10, Sort.by("eventTime").descending());
        List<UserAction> recent = userActionRepository.findRecentByUserId(userId, recentPage);
        if (recent.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> seenEventIds = recent.stream()
                .map(UserAction::getEventId)
                .distinct()
                .toList();

        List<Long> alreadyInteracted = userActionRepository.findEventIdsByUserId(userId);

        List<Pair<Long, EventSimilarity>> candidates = seenEventIds.stream()
                .flatMap(seedId ->
                        similarityRepository.findByEvent(seedId).stream()
                                .map(sim -> Pair.of(seedId, sim))
                )
                .filter(pair -> {
                    long seedId = pair.getFirst();
                    long target = pair.getSecond().getOtherEventId(seedId);
                    return !alreadyInteracted.contains(target);
                })
                .toList();

        Map<Long, List<Pair<Long, EventSimilarity>>> grouped = candidates.stream()
                .collect(Collectors.groupingBy(pair ->
                        pair.getSecond().getOtherEventId(pair.getFirst())
                ));

        Map<Long, Double> knownWeights = recent.stream()
                .collect(Collectors.toMap(UserAction::getEventId, UserAction::getWeight));

        List<RecommendedEventProto> predictions = new ArrayList<>();
        for (Map.Entry<Long, List<Pair<Long, EventSimilarity>>> entry : grouped.entrySet()) {
            Long newEventId = entry.getKey();

            List<EventSimilarity> neighbors = entry.getValue().stream()
                    .map(Pair::getSecond)
                    .sorted(Comparator.comparingDouble(EventSimilarity::getScore).reversed())
                    .limit(10)
                    .toList();

            double numerator = 0.0, denominator = 0.0;
            for (EventSimilarity sim : neighbors) {
                long knownEventId = sim.getOtherEventId(newEventId);
                Double weight = knownWeights.get(knownEventId);
                if (weight == null) {
                    continue;
                }
                numerator += weight * sim.getScore();
                denominator += sim.getScore();
            }
            if (denominator == 0.0) {
                continue;
            }

            double predictedScore = numerator / denominator;
            predictions.add(RecommendedEventProto.newBuilder()
                    .setEventId(newEventId)
                    .setScore(predictedScore)
                    .build());
        }

        return predictions.stream()
                .sorted(Comparator.comparingDouble(RecommendedEventProto::getScore).reversed())
                .limit(maxResults)
                .toList();
    }

    @Override
    public List<RecommendedEventProto> getInteractionsCount(List<Long> eventIds) {
        log.info("RecommendationService - Расчет количества взаимодействий для событий = {}", eventIds);

        return eventIds.stream()
                .map(eventId -> {
                    Double sum = userActionRepository.sumWeightsByEventId(eventId);
                    double sumWeights = sum != null ? sum : 0.0;
                    return RecommendedEventProto.newBuilder()
                            .setEventId(eventId)
                            .setScore(sumWeights)
                            .build();
                })
                .collect(Collectors.toList());
    }
}