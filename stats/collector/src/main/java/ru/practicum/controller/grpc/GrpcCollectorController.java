package ru.practicum.controller.grpc;

import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.practicum.grpc.stats.collector.UserActionControllerGrpc;
import ru.practicum.grpc.stats.message.UserActionProto;
import ru.practicum.handler.UserActionHandler;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class GrpcCollectorController extends UserActionControllerGrpc.UserActionControllerImplBase {

    private final UserActionHandler handler;

    @Override
    public void collectUserAction(UserActionProto request, StreamObserver<Empty> responseObserver) {

        log.info("UserActionController - Получен данные {}", request);

        try {
            handler.handle(request);
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
            log.info("UserActionController - Данные о действиях пользователя собраны");
        } catch (Exception e) {
            log.error("Ошибка при обработке действия пользователя: {}", e.getMessage(), e);

            responseObserver.onError(Status.INTERNAL
                    .withDescription("Не удалось обработать действие пользователя!")
                    .withCause(e)
                    .asRuntimeException()
            );
        }
    }
}