package ru.practicum.service.handler;

import ru.practicum.ewm.stats.avro.UserActionAvro;

public interface UserActionService {

    void handleUserAction(UserActionAvro message);

}
