package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.EventFullDto;
import ru.practicum.service.EventService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/events")
public class InternalEventController {

    private final EventService eventService;

    @GetMapping("/{eventId}")
    public ResponseEntity<EventFullDto> getEventByEventId(@PathVariable Long eventId) {
        log.info("Получение события по id = {}", eventId);
        return ResponseEntity.ok(eventService.getEventByEventId(eventId));
    }
}