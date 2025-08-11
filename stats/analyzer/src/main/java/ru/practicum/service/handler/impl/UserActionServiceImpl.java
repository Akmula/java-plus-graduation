package ru.practicum.service.handler.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.entity.UserAction;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.repository.UserActionRepository;
import ru.practicum.service.handler.UserActionService;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserActionServiceImpl implements UserActionService {

    private final UserActionRepository repository;

    @Override
    public void handleUserAction(UserActionAvro message) {
        long userId = message.getUserId();
        long eventId = message.getEventId();
        double weight = mapActionToWeight(message);

        log.info("UserActionService - запись действий пользователя");

        repository.findAll().stream()
                .filter(e -> e.getUserId().equals(userId) && e.getEventId().equals(eventId))
                .findFirst()
                .ifPresentOrElse(
                        existing -> {
                            if (weight > existing.getWeight()) {
                                existing.setWeight(weight);
                                existing.setTimestamp(Instant.now());
                                repository.save(existing);
                            } else {
                                log.debug("UserActionService - ошибка записи действий пользователя");
                            }
                        },
                        () -> {
                            UserAction newRecord = UserAction.builder()
                                    .userId(userId)
                                    .eventId(eventId)
                                    .weight(weight)
                                    .timestamp(Instant.now())
                                    .build();
                            repository.save(newRecord);
                            log.info("UserActionService - запись действий: userId={}, eventId={}, weight={}",
                                    userId, eventId, weight);
                        }
                );
    }

    private double mapActionToWeight(UserActionAvro action) {
        return switch (action.getActionType()) {
            case VIEW -> 0.4;
            case REGISTER -> 0.8;
            case LIKE -> 1.0;
        };
    }
}