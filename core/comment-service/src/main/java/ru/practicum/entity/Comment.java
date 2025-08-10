package ru.practicum.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "author_id")
    private Long authorId;

    @Column(name = "event_id")
    private Long eventId;

    @Column(nullable = false, length = 500)
    private String text;

    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn;
}