package ru.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.practicum.circuitbreaker.InternalRequestFeignClientFallback;
import ru.practicum.dto.ParticipationRequestDto;

import java.util.List;

@FeignClient(name = "request-service",
        contextId = "InternalRequestFeignClient",
        path = "/internal",
        fallback = InternalRequestFeignClientFallback.class)
public interface InternalRequestFeignClient {

    @GetMapping("/events/{eventId}/requests")
    List<ParticipationRequestDto> getRequestsByEventId(@PathVariable Long eventId);

    @PostMapping("/requests/save")
    ResponseEntity<Object> saveAll(@RequestBody List<ParticipationRequestDto> requests);
}