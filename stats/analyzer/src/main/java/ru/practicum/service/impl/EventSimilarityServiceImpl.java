package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.entity.EventSimilarity;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.repository.EventSimilarityRepository;
import ru.practicum.service.EventSimilarityService;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventSimilarityServiceImpl implements EventSimilarityService {

    private final EventSimilarityRepository repository;

    @Override
    public void handleEventSimilarity(EventSimilarityAvro message) {
        long eventA = message.getEventA();
        long eventB = message.getEventB();
        double score = message.getScore();

        log.info("EventSimilarityService - Получение похожих событий: eventA={}, eventB={}, score={}",
                eventA, eventB, score);

        repository.findAll().stream()
                .filter(e -> (e.getEventA().equals(eventA) && e.getEventB().equals(eventB)) ||
                        (e.getEventA().equals(eventB) && e.getEventB().equals(eventA)))
                .findFirst()
                .ifPresentOrElse(
                        existing -> {
                            existing.setScore(score);
                            repository.save(existing);
                        },
                        () -> {
                            EventSimilarity newRecord = EventSimilarity.builder()
                                    .eventA(eventA)
                                    .eventB(eventB)
                                    .score(score)
                                    .build();
                            repository.save(newRecord);
                        }
                );
    }
}