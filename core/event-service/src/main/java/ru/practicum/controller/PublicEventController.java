package ru.practicum.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.EventShortDto;
import ru.practicum.dto.params.EventParamsPublic;
import ru.practicum.grpc.stats.message.RecommendedEventProto;
import ru.practicum.service.EventService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class PublicEventController {

    private final EventService eventService;

    @GetMapping
    public ResponseEntity<List<EventShortDto>> getEvents(@ModelAttribute EventParamsPublic params,
                                                         HttpServletRequest request) {
        log.info("PublicEventController - Get public events. params: {}", params);
        return ResponseEntity.ok(eventService.getPublicEvents(params, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventFullDto> getEventByIdAndUserId(@PathVariable Long id,
                                                              @RequestHeader("X-EWM-USER-ID") long userId,
                                                              HttpServletRequest request) {
        log.info("PublicEventController - Get public eventId and userId: {}, {}", id, userId);
        return ResponseEntity.ok(eventService.getEventByIdAndUserId(id, userId, request));
    }

    @GetMapping("/recommendations")
    public ResponseEntity<List<RecommendedEventProto>> getRecommendationsForUser(
            @RequestHeader("X-EWM-USER-ID") Long userId,
            @RequestParam(defaultValue = "5") Integer maxResults) {
        log.info("PublicEventController - Get recommendations for userId: {}, maxResults: {}", userId, maxResults);
        return ResponseEntity.ok(eventService.getRecommendationsForUser(userId, maxResults));
    }

    @PutMapping("/{eventId}/like")
    public ResponseEntity<Void> sendLikeToCollector(@RequestHeader("X-EWM-USER-ID") Long userId,
                                                    @PathVariable Long eventId) {
        log.info("PublicEventController - Send like to Collector. userId: {}, eventId: {}", userId, eventId);
        eventService.sendLikeToCollector(userId, eventId);
        return ResponseEntity.noContent().build();
    }
}