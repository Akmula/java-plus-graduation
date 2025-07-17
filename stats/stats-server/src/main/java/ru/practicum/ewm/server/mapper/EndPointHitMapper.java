package ru.practicum.ewm.server.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.server.model.EndpointHit;

@Component
public class EndPointHitMapper {

    public EndpointHit mapToHit(EndpointHitDto endpointHitDto) {
        EndpointHit endpointHit = new EndpointHit();
        endpointHit.setApp(endpointHitDto.getApp());
        endpointHit.setUri(endpointHitDto.getUri());
        endpointHit.setIp(endpointHitDto.getIp());
        endpointHit.setTimestamp(endpointHitDto.getTimestamp());
        return endpointHit;
    }
}