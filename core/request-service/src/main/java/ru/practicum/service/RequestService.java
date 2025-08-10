package ru.practicum.service;

import org.springframework.web.bind.annotation.RequestBody;
import ru.practicum.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    List<ParticipationRequestDto> getUserRequests(Long userId);

    ParticipationRequestDto addRequest(Long userId, Long eventId);

    ParticipationRequestDto cancelRequest(Long userId, Long requestId);

    List<ParticipationRequestDto> getRequestsByEventId(Long eventId);

    void saveAll(@RequestBody List<ParticipationRequestDto> requests);
}