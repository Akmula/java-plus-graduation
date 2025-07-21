package ru.practicum.ewm.main.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.dto.ParticipationRequestDto;
import ru.practicum.ewm.main.service.RequestService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/requests")
public class PrivateRequestController {

    private final RequestService requestService;

    @GetMapping
    public ResponseEntity<List<ParticipationRequestDto>> getUserRequests(@PathVariable Long userId) {
        log.info("Getting all user requests by their id: {}", userId);
        return ResponseEntity.ok(requestService.getUserRequests(userId));
    }

    @PostMapping
    public ResponseEntity<ParticipationRequestDto> addRequest(@PathVariable Long userId,
                                                              @RequestParam Long eventId) {
        log.info("Creating new request by user with id: {} for event with id: {}", userId, eventId);
        return ResponseEntity.status(201).body(requestService.addRequest(userId, eventId));
    }

    @PatchMapping("/{requestId}/cancel")
    public ResponseEntity<ParticipationRequestDto> cancelRequest(@PathVariable Long userId,
                                                                 @PathVariable Long requestId) {
        log.info("Deleting request with id: {} by user with id: {}", requestId, userId);
        return ResponseEntity.ok(requestService.cancelRequest(userId, requestId));
    }
}
