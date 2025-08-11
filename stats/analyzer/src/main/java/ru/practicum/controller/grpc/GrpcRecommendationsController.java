package ru.practicum.controller.grpc;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.practicum.grpc.stats.analyzer.RecommendationsControllerGrpc;
import ru.practicum.grpc.stats.message.InteractionsCountRequestProto;
import ru.practicum.grpc.stats.message.RecommendedEventProto;
import ru.practicum.grpc.stats.message.SimilarEventsRequestProto;
import ru.practicum.grpc.stats.message.UserPredictionsRequestProto;
import ru.practicum.service.RecommendationService;

import java.util.List;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class GrpcRecommendationsController extends RecommendationsControllerGrpc.RecommendationsControllerImplBase {

    private final RecommendationService recommendationService;

    @Override
    public void getSimilarEvents(SimilarEventsRequestProto request,
                                 StreamObserver<RecommendedEventProto> responseObserver) {

        log.info("GrpcRecommendationsController - Получение похожий событий: {}", request);

        try {
            List<RecommendedEventProto> recommendations = recommendationService.getSimilarEvents(request);
            recommendations.forEach(responseObserver::onNext);
            responseObserver.onCompleted();

            log.info("GrpcRecommendationsController -  Получено {} похожих событий", recommendations.size());
        } catch (Exception e) {
            log.error("GrpcRecommendationsController - Ошибка при получении похожих событий: {}", e.getMessage(), e);
            responseObserver.onError(e);
        }
    }

    @Override
    public void getRecommendationsForUser(UserPredictionsRequestProto request,
                                          StreamObserver<RecommendedEventProto> responseObserver) {
        log.info("GrpcRecommendationsController - получение рекомендаций для пользователя!");

        try {
            List<RecommendedEventProto> recommendations = recommendationService.getRecommendationsForUser(request);
            recommendations.forEach(responseObserver::onNext);
            responseObserver.onCompleted();

            log.info("GrpcRecommendationsController - получено {} рекомендаций!", recommendations.size());
        } catch (Exception e) {
            log.error("GrpcRecommendationsController - Ошибка при получении рекомендаций: {}", e.getMessage(), e);
            responseObserver.onError(e);
        }
    }

    @Override
    public void getInteractionsCount(InteractionsCountRequestProto request,
                                     StreamObserver<RecommendedEventProto> responseObserver) {
        log.info("GrpcRecommendationsController -  подсчет количества взаимодействий");

        try {
            List<RecommendedEventProto> interactions = recommendationService.getInteractionsCount(request);
            interactions.forEach(responseObserver::onNext);
            responseObserver.onCompleted();

            log.info("GrpcRecommendationsController - Найдено {} взаимодействий!", interactions.size());
        } catch (Exception e) {
            log.error("GrpcRecommendationsController - Ошибка при подсчете взаимодействий: {}", e.getMessage(), e);
            responseObserver.onError(e);
        }
    }
}