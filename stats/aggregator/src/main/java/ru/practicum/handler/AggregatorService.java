package ru.practicum.handler;

import ru.practicum.ewm.stats.avro.UserActionAvro;

public interface AggregatorService {
    void handle(UserActionAvro action);
}