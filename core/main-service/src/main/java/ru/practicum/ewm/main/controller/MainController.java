package ru.practicum.ewm.main.controller;

import ru.practicum.ewm.client.StatsServer;
import ru.practicum.ewm.dto.EndpointHitDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/main")
@RequiredArgsConstructor
public class MainController {
    private final StatsServer statClient;

    @PostMapping("/track")
    public void trackEvent(@RequestParam String uri, @RequestParam String ip) {
        EndpointHitDto hitDto = new EndpointHitDto("main-service", uri, ip, LocalDateTime.now());
        statClient.sendHit(hitDto);
    }
}
