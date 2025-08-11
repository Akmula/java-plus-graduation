package ru.practicum.service;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.dto.*;
import ru.practicum.dto.params.EventParamsAdmin;
import ru.practicum.dto.params.EventParamsPublic;
import ru.practicum.dto.params.UserParamsAdmin;
import ru.practicum.grpc.stats.message.RecommendedEventProto;

import java.util.List;

public interface EventService {

    List<EventShortDto> getUserEvents(Long userId, UserParamsAdmin params);

    EventFullDto getUserEventById(Long userId, Long eventId);

    EventFullDto createUserEvent(Long userId, NewEventDto dto);

    EventFullDto updateUserEvent(Long userId, Long eventId, UpdateEventUserRequest dto);

    List<EventShortDto> getPublicEvents(EventParamsPublic params, HttpServletRequest request);

    EventFullDto getEventByIdAndUserId(Long eventId, Long UserId, HttpServletRequest request);

    List<EventFullDto> getEventsByAdmin(EventParamsAdmin params);

    EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest dto);

    EventRequestStatusUpdateResult updateRequestStatus(Long userId, Long eventId,
                                                       EventRequestStatusUpdateRequest requestUpdate);

    List<ParticipationRequestDto> getAllParticipationRequestsByUserIdAndEventId(Long userId, Long eventId);

    EventFullDto getEventByEventId(Long eventId);

    List<RecommendedEventProto> getRecommendationsForUser(Long userId, Integer maxResults);

    void sendLikeToCollector(Long userId, Long eventId);
}