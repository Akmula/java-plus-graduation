package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.entity.UserAction;

import java.util.List;

public interface UserActionRepository extends JpaRepository<UserAction, Long> {

    @Query("SELECT u.eventId FROM UserAction u WHERE u.userId = :userId")
    List<Long> findEventIdsByUserId(Long userId);

    @Query("SELECT ua.eventId, SUM(ua.weight) FROM UserAction ua WHERE ua.eventId IN :eventIds " +
            "GROUP BY ua.eventId")
    List<Object[]> sumMaxWeightsByEventIds(List<Long> eventIds);

    List<UserAction> findAllByUserIdOrderByTimestamp(Long userId);
}