package ru.practicum.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.kafka.KafkaProducer;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class AggregatorServiceImpl implements AggregatorService {

    private final KafkaProducer producer;

    private final Map<Long, Map<Long, Double>> eventUserWeights = new ConcurrentHashMap<>();
    private final Map<Long, Double> eventWeightSums = new ConcurrentHashMap<>();
    private final Map<Long, Map<Long, Double>> minWeightsSums = new ConcurrentHashMap<>();
    private final Map<ActionTypeAvro, Double> actionWeights = Map.of(
            ActionTypeAvro.VIEW, 0.4,
            ActionTypeAvro.REGISTER, 0.8,
            ActionTypeAvro.LIKE, 1.0
    );

    @Override
    public void handle(UserActionAvro action) {
        long eventId = action.getEventId();
        long userId = action.getUserId();
        Instant timestamp = action.getTimestamp();

        double newWeight = actionWeights.getOrDefault(action.getActionType(), 0.0);

        Map<Long, Double> userWeights = eventUserWeights.computeIfAbsent(eventId, k -> new ConcurrentHashMap<>());
        double currentWeight = userWeights.getOrDefault(userId, 0.0);

        if (currentWeight >= newWeight) return;

        userWeights.put(userId, newWeight);

        double oldSum = eventWeightSums.getOrDefault(eventId, 0.0);
        double diff = newWeight - currentWeight;
        eventWeightSums.put(eventId, oldSum + diff);

        for (long otherEventId : eventUserWeights.keySet()) {
            if (eventId == otherEventId) continue;

            long a = Math.min(eventId, otherEventId);
            long b = Math.max(eventId, otherEventId);

            double weightA = eventUserWeights.getOrDefault(eventId, new HashMap<>()).getOrDefault(userId, 0.0);
            double weightB = eventUserWeights.getOrDefault(otherEventId, new HashMap<>()).getOrDefault(userId, 0.0);

            if (weightB == 0.0) continue;

            Map<Long, Double> minSums = minWeightsSums.computeIfAbsent(a, k -> new ConcurrentHashMap<>());
            double currentMinSum = minSums.getOrDefault(b, 0.0);
            double oldMin = Math.min(currentWeight, weightB);
            double newMin = Math.min(newWeight, weightB);
            minSums.put(b, currentMinSum + (newMin - oldMin));

            double minSum = minSums.get(b);
            double normA = eventWeightSums.getOrDefault(a, 0.0);
            double normB = eventWeightSums.getOrDefault(b, 0.0);

            double similarity = (normA > 0 && normB > 0)
                    ? minSum / Math.sqrt(normA * normB)
                    : 0.0;

            EventSimilarityAvro similarityAvro = EventSimilarityAvro.newBuilder()
                    .setEventA(a)
                    .setEventB(b)
                    .setScore(similarity)
                    .setTimestamp(timestamp)
                    .build();

            producer.send(similarityAvro);
        }
    }
}