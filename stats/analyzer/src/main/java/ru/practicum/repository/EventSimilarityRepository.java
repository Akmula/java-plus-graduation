package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.entity.EventSimilarity;

import java.util.List;

public interface EventSimilarityRepository extends JpaRepository<EventSimilarity, Long> {

    @Query("SELECT e FROM EventSimilarity e WHERE e.eventA = :eventId OR e.eventB = :eventId")
    List<EventSimilarity> findByEvent(Long eventId);

}