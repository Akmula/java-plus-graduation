package ru.practicum.controller.grpc;

import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.grpc.stats.collector.UserActionControllerGrpc;
import ru.practicum.grpc.stats.message.UserActionProto;
import ru.practicum.kafka.CollectorKafkaProducer;
import ru.practicum.kafka.KafkaProperties;
import ru.practicum.mapper.UserActionMapper;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class GrpcCollectorController extends UserActionControllerGrpc.UserActionControllerImplBase {

    private final CollectorKafkaProducer producer;
    private final KafkaProperties kafkaProperties;
    private final UserActionMapper mapper;

    @Override
    public void collectUserAction(UserActionProto request,
                                  StreamObserver<Empty> responseObserver) {
        try {
            log.info("CollectorController - Получен новый запрос");

            UserActionAvro avroMessage = mapper.mapToAvro(request);

            String topic = kafkaProperties.getProducer().getTopic();
            Long key = request.getUserId(); // key = user id

            producer.send(topic, key, avroMessage);

            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
            log.info("CollectorController - запрос обработан.");
        } catch (Exception e) {
            log.error("Ошибка при обработке действия пользователя: {}", e.getMessage(), e);

            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription("Ошибка при обработке действия пользователя")
                            .withCause(e)
                            .asRuntimeException()
            );
        }
    }
}