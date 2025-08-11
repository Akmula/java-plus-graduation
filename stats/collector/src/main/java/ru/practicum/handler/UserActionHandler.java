package ru.practicum.handler;

import ru.practicum.grpc.stats.message.UserActionProto;

public interface UserActionHandler {

    void handle(UserActionProto userActionProto);
}