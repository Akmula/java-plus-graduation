package ru.practicum.ewm.client;

import com.google.protobuf.Timestamp;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.practicum.grpc.stats.collector.UserActionControllerGrpc;
import ru.practicum.grpc.stats.message.ActionTypeProto;
import ru.practicum.grpc.stats.message.UserActionProto;

import java.time.Instant;

@Slf4j
@Service
public class UserActionClient {

    @GrpcClient("collector")
    private UserActionControllerGrpc.UserActionControllerBlockingStub client;

    public void sendAction(long userId, long eventId, ActionTypeProto actionType, Instant timestamp) {

        log.info("UserActionClient — Пользователь с id: {}, совершил действие: {} в событии с id: {}.",
                userId, eventId, actionType);

        if (client == null) {
            log.warn("UserActionClient — клиент не существует, отправка пропущена.");
            return;
        }

        try {
            UserActionProto request = UserActionProto.newBuilder()
                    .setUserId(userId)
                    .setEventId(eventId)
                    .setActionType(actionType)
                    .setTimestamp(Timestamp.newBuilder()
                            .setSeconds(timestamp.getEpochSecond())
                            .setNanos(timestamp.getNano())
                            .build())
                    .build();

            client.collectUserAction(request);

            log.info("UserActionClient — действие: {}, отправлено в collector.", request);
        } catch (Exception e) {
            log.warn("UserActionClient - ошибка при отправке действия в collector: {}", e.getMessage());
            throw new RuntimeException("UserActionClient - ошибка при отправке действия", e);
        }
    }
}