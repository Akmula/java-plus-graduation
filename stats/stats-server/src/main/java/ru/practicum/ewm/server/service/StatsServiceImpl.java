package ru.practicum.ewm.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.ViewStatsDto;
import ru.practicum.ewm.server.mapper.EndPointHitMapper;
import ru.practicum.ewm.server.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsServiceImpl implements StatsService {

    private final StatsRepository repository;
    private final EndPointHitMapper mapper;

    @Override
    @Transactional
    public void save(EndpointHitDto hitDto) {
        repository.save(mapper.mapToHit(hitDto));
    }

    @Override
    public List<ViewStatsDto> findStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        if (uris == null && !unique) {
            return repository.findStatsByTimestamp(start, end);
        } else if (uris == null) {
            return repository.findStatsByTimestampAndUnique(start, end);
        } else if (!unique) {
            return repository.findStatsByTimestampAndUri(start, end, uris);
        } else {
            return repository.findStatsByTimestampAndUniqueAndUri(start, end, uris);
        }
    }
}