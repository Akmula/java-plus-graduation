package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.entity.UserAction;

import java.util.List;

public interface UserActionRepository extends JpaRepository<UserAction, Long> {

    @Query("SELECT u.eventId FROM UserAction u WHERE u.userId = :userId")
    List<Long> findEventIdsByUserId(Long userId);

    @Query("SELECT u FROM UserAction u WHERE u.userId = :userId ORDER BY u.timestamp DESC")
    List<UserAction> findRecentByUserId(Long userId, org.springframework.data.domain.Pageable pageable);

    @Query("SELECT SUM(u.weight) FROM UserAction u WHERE u.eventId = :eventId")
    Double sumWeightsByEventId(Long eventId);

}