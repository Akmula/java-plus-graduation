package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.client.InternalRequestFeignClient;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.service.RequestService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/internal")
public class InternalRequestController implements InternalRequestFeignClient {

    private final RequestService requestService;

    @Override
    public List<ParticipationRequestDto> getRequestsByEventId(Long eventId) {
        log.info("Internal - Получение списка запросов на участие в событии с id: {}", eventId);
        return requestService.getRequestsByEventId(eventId);
    }

    @Override
    public ResponseEntity<Object> saveAll(List<ParticipationRequestDto> requests) {
        log.info("Internal - Сохранение списка запросов на участие в событии: {}", requests);
        requestService.saveAll(requests);
        return ResponseEntity.noContent().build();
    }
}