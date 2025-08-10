package ru.practicum.circuitbreaker;

import org.springframework.stereotype.Component;
import ru.practicum.client.InternalEventFeignClient;
import ru.practicum.dto.EventFullDto;

@Component
public class InternalEventFeignClientFallback implements InternalEventFeignClient {

    @Override
    public EventFullDto getEventByEventId(Long eventId) {
        return null;
    }
}