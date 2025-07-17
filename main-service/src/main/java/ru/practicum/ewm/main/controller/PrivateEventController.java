package ru.practicum.ewm.main.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.dto.*;
import ru.practicum.ewm.main.dto.params.UserParamsAdmin;
import ru.practicum.ewm.main.service.EventService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events")
public class PrivateEventController {

    private final EventService eventService;

    @GetMapping
    public ResponseEntity<List<EventShortDto>> getAll(@PathVariable Long userId, @Valid UserParamsAdmin params) {
        log.info("PrivateEventController - getAll");
        return ResponseEntity.ok(eventService.getUserEvents(userId, params));
    }

    @PostMapping
    public ResponseEntity<EventFullDto> create(@PathVariable Long userId,
                                               @RequestBody @Valid NewEventDto dto) {
        log.info("PrivateEventController - create new event for {}", dto);
        return ResponseEntity.status(201).body(eventService.createUserEvent(userId, dto));
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventFullDto> getById(@PathVariable Long userId,
                                                @PathVariable Long eventId) {
        log.info("PrivateEventController - getById eventId = {}", eventId);
        return ResponseEntity.ok(eventService.getUserEventById(userId, eventId));
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<EventFullDto> update(@PathVariable Long userId,
                                               @PathVariable Long eventId,
                                               @RequestBody @Valid UpdateEventUserRequest dto) {
        log.info("PrivateEventController - patch update eventId = {}", eventId);
        return ResponseEntity.ok(eventService.updateUserEvent(userId, eventId, dto));
    }

    @GetMapping("/{eventId}/requests")
    public ResponseEntity<List<ParticipationRequestDto>> getRequests(@PathVariable Long userId,
                                                                     @PathVariable Long eventId) {
        log.info("PrivateEventController - getRequests eventId = {}", eventId);
        return ResponseEntity.ok(eventService.getAllParticipationRequestsByUserIdAndEventId(userId, eventId));
    }

    @PatchMapping("/{eventId}/requests")
    public ResponseEntity<EventRequestStatusUpdateResult> updateRequests(@PathVariable Long userId,
                                                                         @PathVariable Long eventId,
                                                                         @RequestBody @Valid
                                                                         EventRequestStatusUpdateRequest request) {
        log.info("PrivateEventController - updateRequests eventId = {}", eventId);
        return ResponseEntity.ok(eventService.updateRequestStatus(userId, eventId, request));
    }
}