package ru.practicum.ewm.main.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.ewm.main.model.enums.EventState;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String title;

    @Column(columnDefinition = "TEXT")
    private String annotation;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "event_date")
    private LocalDateTime eventDate;

    @Column(name = "created_on")
    private LocalDateTime createdOn;

    @Column(name = "published_on")
    private LocalDateTime publishedOn;

    private boolean paid;

    @Column(name = "participant_limit")
    private Integer participantLimit;

    @Column(name = "request_moderation")
    private boolean requestModeration;

    @Enumerated(EnumType.STRING)
    private EventState state;

    @Embedded
    private Location location;

    @ManyToOne
    @JoinColumn(name = "initiator_id")
    private User initiator;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
}