package ru.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.circuitbreaker.InternalEventFeignClientFallback;
import ru.practicum.dto.EventFullDto;

@FeignClient(name = "event-service",
        contextId = "InternalEventFeignClient",
        path = "/internal/events",
        fallback = InternalEventFeignClientFallback.class)
public interface InternalEventFeignClient {

    @GetMapping("/{eventId}")
    EventFullDto getEventByEventId(@PathVariable Long eventId);
}