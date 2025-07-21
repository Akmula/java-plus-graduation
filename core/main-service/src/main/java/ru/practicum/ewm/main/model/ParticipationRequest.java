package ru.practicum.ewm.main.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.ewm.main.model.enums.ParticipationRequestStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "participation_requests")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParticipationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime created;

    @Enumerated(EnumType.STRING)
    private ParticipationRequestStatus status;

    @ManyToOne
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;
}