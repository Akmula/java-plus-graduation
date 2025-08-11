package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.entity.EventSimilarity;
import ru.practicum.entity.UserAction;
import ru.practicum.grpc.stats.message.InteractionsCountRequestProto;
import ru.practicum.grpc.stats.message.RecommendedEventProto;
import ru.practicum.grpc.stats.message.SimilarEventsRequestProto;
import ru.practicum.grpc.stats.message.UserPredictionsRequestProto;
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
    private final EventSimilarityRepository eventSimilarityRepository;

    @Override
    public List<RecommendedEventProto> getSimilarEvents(SimilarEventsRequestProto request) {
        log.info("RecommendationService - получение похожих событий.");

        Long userId = request.getUserId();
        Long eventId = request.getEventId();
        int maxResults = request.getMaxResults();

        List<EventSimilarity> similarities = eventSimilarityRepository.findByEventId(eventId);

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
    public List<RecommendedEventProto> getRecommendationsForUser(UserPredictionsRequestProto request) {
        log.info("RecommendationService - получение рекомендаций");

        Long userId = request.getUserId();
        int maxResults = request.getMaxResults();

        List<UserAction> userActions = userActionRepository.findAllByUserIdOrderByTimestamp(userId);

        List<Long> interactedEventIds = userActions.stream()
                .map(UserAction::getEventId)
                .toList();

        Set<Long> candidateEventIds = interactedEventIds.stream()
                .flatMap(eventId -> {
                    List<EventSimilarity> similarities = eventSimilarityRepository.findByEventAAndEventB(eventId, eventId);
                    return similarities.stream()
                            .flatMap(s -> {
                                Set<Long> events = new HashSet<>();
                                events.add(s.getEventA());
                                events.add(s.getEventB());
                                return events.stream();
                            });
                })
                .filter(eventId -> !interactedEventIds.contains(eventId))
                .collect(Collectors.toSet());


        return candidateEventIds.stream()
                .map(eventId -> {
                    List<EventSimilarity> neighbors = eventSimilarityRepository.findByEventAAndEventB(eventId, eventId)
                            .stream()
                            .filter(s -> interactedEventIds.contains(s.getEventA()) ||
                                    interactedEventIds.contains(s.getEventB()))
                            .sorted(Comparator.comparing(EventSimilarity::getScore).reversed())
                            .toList();

                    double weightedSum = 0.0;
                    double similaritySum = 0.0;
                    for (EventSimilarity neighbor : neighbors) {
                        long neighborEventId = neighbor.getEventA().equals(eventId) ?
                                neighbor.getEventB() : neighbor.getEventA();
                        UserAction action = userActions.stream()
                                .filter(a -> a.getEventId().equals(neighborEventId))
                                .findFirst()
                                .orElse(null);
                        if (Objects.nonNull(action)) {
                            weightedSum += neighbor.getScore() * action.getWeight();
                            similaritySum += neighbor.getScore();
                        }
                    }

                    double score = similaritySum > 0 ? weightedSum / similaritySum : 0.0;

                    return RecommendedEventProto.newBuilder()
                            .setEventId(eventId)
                            .setScore(score)
                            .build();
                })
                .sorted(Comparator.comparingDouble(RecommendedEventProto::getScore).reversed())
                .limit(maxResults).toList();
    }


    @Override
    public List<RecommendedEventProto> getInteractionsCount(InteractionsCountRequestProto request) {
        log.info("RecommendationService - подсчет количества взаимодействий");

        List<Long> eventIds = request.getEventIdList();

        List<Object[]> weightSums = userActionRepository.sumMaxWeightsByEventIds(eventIds);
        Map<Long, Double> weights = weightSums.stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0], // event_id
                        row -> (Double) row[1], // sum of weights
                        (a, b) -> a
                ));
        List<RecommendedEventProto> results = eventIds.stream()
                .map(eventId -> RecommendedEventProto.newBuilder()
                        .setEventId(eventId)
                        .setScore(weights.getOrDefault(eventId, 0.0))
                        .build())
                .toList();

        return results.stream().toList();
    }
}