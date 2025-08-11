package ru.practicum.circuitbreaker;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.practicum.client.InternalRequestFeignClient;
import ru.practicum.dto.ParticipationRequestDto;

import java.util.List;

@Component
public class InternalRequestFeignClientFallback implements InternalRequestFeignClient {

    @Override
    public List<ParticipationRequestDto> getRequestsByEventId(Long eventId) {
        return List.of();
    }

    @Override
    public ResponseEntity<Object> saveAll(List<ParticipationRequestDto> requests) {
        return null;
    }

    @Override
    public ParticipationRequestDto getRequestByEventIdAndUserId(Long eventId, Long userId) {
        return null;
    }
}