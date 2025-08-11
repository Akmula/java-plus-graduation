package ru.practicum.service.handler;

import ru.practicum.ewm.stats.avro.EventSimilarityAvro;

public interface EventSimilarityService {

    void handleEventSimilarity(EventSimilarityAvro message);

}