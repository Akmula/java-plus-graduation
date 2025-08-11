package ru.practicum.service;

import ru.practicum.grpc.stats.message.InteractionsCountRequestProto;
import ru.practicum.grpc.stats.message.RecommendedEventProto;
import ru.practicum.grpc.stats.message.SimilarEventsRequestProto;
import ru.practicum.grpc.stats.message.UserPredictionsRequestProto;

import java.util.List;

public interface RecommendationService {

    List<RecommendedEventProto> getSimilarEvents(SimilarEventsRequestProto request);

    List<RecommendedEventProto> getRecommendationsForUser(UserPredictionsRequestProto request);

    List<RecommendedEventProto> getInteractionsCount(InteractionsCountRequestProto request);

}