package ru.practicum.ewm.main.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.main.model.Compilation;

import java.util.Optional;


public interface CompilationRepository extends JpaRepository<Compilation, Long> {

    @Query("SELECT c FROM Compilation c LEFT JOIN FETCH c.events WHERE c.pinned = :pinned")
    Page<Compilation> findAllByPinned(@Param("pinned") boolean pinned, Pageable pageable);

    @Query("SELECT c FROM Compilation c LEFT JOIN FETCH c.events")
    Page<Compilation> findAll(Pageable pageable);

    Optional<Compilation> findByTitle(String title);
}