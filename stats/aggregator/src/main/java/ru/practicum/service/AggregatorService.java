package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.kafka.AggregatorKafkaProducer;
import ru.practicum.kafka.KafkaProperties;

import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AggregatorService {

    private final AggregatorKafkaProducer producer;
    private final KafkaProperties properties;

    private final Map<Long, Map<Long, Double>> weightMatrix = new HashMap<>();
    private final Map<Long, Map<Long, Double>> similarities = new HashMap<>();
    private final Map<Long, Map<Long, Double>> minWeightsSums = new HashMap<>();
    private final Map<Long, Double> eventTotalSums = new HashMap<>();

    public void processUserAction(UserActionAvro action) {
        long userId = action.getUserId();
        long eventId = action.getEventId();
        double newWeight = getActionWeight(action);

        log.info("AggregatorService - Получено действие пользователя: userId={}, eventId={}, type={}, weight={}",
                userId, eventId, action.getActionType(), newWeight);

        boolean isNewEvent = !weightMatrix.containsKey(eventId);

        if (isNewEvent) {
            handleNewEvent(eventId, userId, newWeight);
        } else {
            handleExistingEvent(eventId, userId, newWeight);
        }
    }

    private void handleNewEvent(long eventId, long userId, double newWeight) {
        log.info("AggregatorService - Обнаружено новое событие: eventId={}", eventId);

        Map<Long, Double> userWeights = new HashMap<>();
        userWeights.put(userId, newWeight);
        weightMatrix.put(eventId, userWeights);
        eventTotalSums.put(eventId, newWeight);

        for (Long otherEventId : weightMatrix.keySet()) {
            if (otherEventId.equals(eventId)) continue;

            if (isOtherWeightZero(userId, otherEventId)) continue;

            double S_min = getSMin(eventId, otherEventId);
            saveSMinToMemory(eventId, otherEventId, S_min);

            double sumA = eventTotalSums.get(eventId);
            double sumB = eventTotalSums.get(otherEventId);

            double similarity = S_min / Math.sqrt(sumA * sumB);

            saveAndSendSimilarity(eventId, otherEventId, similarity);
        }
    }

    private void saveSMinToMemory(long eventId, Long otherEventId, double S_min) {
        long first = Math.min(eventId, otherEventId);
        long second = Math.max(eventId, otherEventId);
        minWeightsSums
                .computeIfAbsent(first, f -> new HashMap<>())
                .put(second, S_min);
    }

    private double getSMin(long eventId, Long otherEventId) {
        var allUsers = new HashSet<Long>();
        allUsers.addAll(weightMatrix.get(eventId).keySet());
        allUsers.addAll(weightMatrix.get(otherEventId).keySet());

        double S_min = 0.0;
        for (Long u : allUsers) {
            double wA = weightMatrix.get(eventId).getOrDefault(u, 0.0);
            double wB = weightMatrix.get(otherEventId).getOrDefault(u, 0.0);
            S_min += Math.min(wA, wB);
        }
        return S_min;
    }

    private boolean isOtherWeightZero(long userId, Long otherEventId) {
        double otherWeight = weightMatrix.get(otherEventId).getOrDefault(userId, 0.0);
        return otherWeight == 0.0;
    }

    private void handleExistingEvent(long eventId, long userId, double newWeight) {
        Map<Long, Double> userWeights = weightMatrix.get(eventId); //userId -> weight
        double oldWeight = userWeights.getOrDefault(userId, 0.0);

        if (newWeight <= oldWeight) {
            return;
        }
        userWeights.put(userId, newWeight);
        double newSumA = getAndSaveSumANew(eventId, newWeight - oldWeight);

        for (Long otherEventId : weightMatrix.keySet()) {
            if (otherEventId.equals(eventId)) continue;
            if (isOtherWeightZero(userId, otherEventId)) continue;

            double otherWeight = weightMatrix.get(otherEventId).get(userId);
            double minOld = Math.min(oldWeight, otherWeight);
            double minNew = Math.min(newWeight, otherWeight);
            double minDelta = minNew - minOld;
            double S_min_new = getAndSaveSMinNew(eventId, otherEventId, minDelta);
            double sumB = eventTotalSums.get(otherEventId);
            double similarity = S_min_new / Math.sqrt(newSumA * sumB);

            saveAndSendSimilarity(eventId, otherEventId, similarity);
        }
    }

    private double getAndSaveSumANew(long eventId, double deltaWeight) {
        double sumA_old = eventTotalSums.get(eventId);
        double sumA_new = sumA_old + deltaWeight;
        eventTotalSums.put(eventId, sumA_new);
        return sumA_new;
    }

    private double getAndSaveSMinNew(long eventId, Long otherEventId, double minDelta) {
        long first = Math.min(eventId, otherEventId);
        long second = Math.max(eventId, otherEventId);
        double S_min_old = minWeightsSums
                .computeIfAbsent(first, k -> new HashMap<>())
                .getOrDefault(second, 0.0);
        double S_min_new = S_min_old + minDelta;

        if (S_min_new > S_min_old) {
            minWeightsSums
                    .computeIfAbsent(first, k -> new HashMap<>())
                    .put(second, S_min_new);
        }
        return S_min_new;
    }

    private void saveAndSendSimilarity(long eventA, long eventB, double similarity) {
        long first = Math.min(eventA, eventB);
        long second = Math.max(eventA, eventB);
        similarities
                .computeIfAbsent(first, f -> new HashMap<>())
                .put(second, similarity);
        sendSimilarity(first, second, similarity);
    }

    private void sendSimilarity(long first, long second, double similarity) {
        EventSimilarityAvro similarityAvro = EventSimilarityAvro.newBuilder()
                .setEventA(first)
                .setEventB(second)
                .setScore(similarity)
                .setTimestamp(Instant.ofEpochMilli(Instant.now().toEpochMilli()))
                .build();
        producer.send(properties.getProducer().getTopic(), first, similarityAvro);
    }

    private double getActionWeight(UserActionAvro action) {
        return switch (action.getActionType()) {
            case VIEW -> 0.4;
            case REGISTER -> 0.8;
            case LIKE -> 1.0;
        };
    }
}