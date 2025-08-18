package ru.practicum.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.grpc.stats.message.ActionTypeProto;
import ru.practicum.grpc.stats.message.UserActionProto;

import java.time.Instant;

@Component
public class UserActionMapper {

    public UserActionAvro mapToAvro(UserActionProto proto) {
        return UserActionAvro.newBuilder()
                .setUserId(proto.getUserId())
                .setEventId(proto.getEventId())
                .setActionType(getActionType(proto.getActionType()))
                .setTimestamp(Instant.ofEpochSecond(
                        proto.getTimestamp().getSeconds(),
                        proto.getTimestamp().getNanos()
                ))
                .build();
    }

    private ActionTypeAvro getActionType(ActionTypeProto proto) {
        switch (proto) {
            case ACTION_VIEW -> {
                return ActionTypeAvro.VIEW;
            }
            case ACTION_REGISTER -> {
                return ActionTypeAvro.REGISTER;
            }
            case ACTION_LIKE -> {
                return ActionTypeAvro.LIKE;
            }
            default -> throw new IllegalArgumentException("Неизвестный ActionTypeProto: " + proto);
        }
    }
}
