package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import ru.practicum.client.InternalEventFeignClient;
import ru.practicum.client.InternalUserFeignClient;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.dto.UserDto;
import ru.practicum.entity.ParticipationRequest;
import ru.practicum.enums.EventState;
import ru.practicum.enums.ParticipationRequestStatus;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.ParticipationRequestMapper;
import ru.practicum.repository.ParticipationRequestRepository;
import ru.practicum.service.RequestService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {

    private final ParticipationRequestRepository requestRepository;
    private final InternalUserFeignClient userClient;
    private final InternalEventFeignClient eventClient;


    @Override
    public List<ParticipationRequestDto> getUserRequests(Long userId) {
        getUserById(userId);
        return requestRepository.findAllByRequesterId(userId).stream()
                .map(ParticipationRequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ParticipationRequestDto addRequest(Long requesterId, Long eventId) {
        UserDto requester = getUserById(requesterId);
        EventFullDto event = getEventById(eventId);

        if (requestRepository.existsByRequesterIdAndEventId(requesterId, eventId)) {
            throw new ConflictException("User already sent a request for this event.");
        }
        if (requesterId.equals(event.getInitiator().getId())) {
            throw new ConflictException("The event initiator cannot submit a participation request for their own event.");
        }
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Participation in an unpublished event is not allowed.");
        }

        long confirmedRequests = requestRepository.countByEventIdAndStatus(eventId, ParticipationRequestStatus.CONFIRMED);

        if (confirmedRequests >= event.getParticipantLimit() && event.getParticipantLimit() != 0) {
            throw new ConflictException("The event has reached the participation request limit.");
        }

        ParticipationRequest request = ParticipationRequest.builder()
                .requesterId(requester.getId())
                .eventId(eventId)
                .created(LocalDateTime.now())
                .status(ParticipationRequestStatus.PENDING)
                .build();

        if (!event.isRequestModeration() || event.getParticipantLimit() == 0) {
            request.setStatus(ParticipationRequestStatus.CONFIRMED);
        }

        return ParticipationRequestMapper.toDto(requestRepository.save(request));
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        getUserById(userId);
        ParticipationRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request not found"));

        if (!request.getRequesterId().equals(userId)) {
            throw new ConflictException("User can cancel only their own requests.");
        }

        request.setStatus(ParticipationRequestStatus.CANCELED);
        return ParticipationRequestMapper.toDto(requestRepository.save(request));
    }

    @Override
    public List<ParticipationRequestDto> getRequestsByEventId(Long eventId) {
        List<ParticipationRequest> requests = requestRepository
                .findAllByEventId(eventId);

        return requests.stream()
                .map(ParticipationRequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void saveAll(@RequestBody List<ParticipationRequestDto> requests) {
        List<ParticipationRequest> entities = requests.stream()
                .map(ParticipationRequestMapper::toEntity)
                .collect(Collectors.toList());
        requestRepository.saveAll(entities);
    }

    @Override
    public ParticipationRequestDto getRequestByEventIdAndUserId(Long eventId, Long userId) {
        return ParticipationRequestMapper.toDto(requestRepository.getRequestByEventIdAndRequesterId(eventId, userId));
    }

    private UserDto getUserById(Long userId) {
        return userClient.getByUserId(userId);
    }

    private EventFullDto getEventById(Long eventId) {
        return eventClient.getEventByEventId(eventId);
    }
}