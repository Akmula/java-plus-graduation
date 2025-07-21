package ru.practicum.ewm.main.dto;

import lombok.*;
import jakarta.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateEventUserRequest {

    @Size(min = 3, max = 120, message = "Title must be between 3 and 120 characters")
    private String title;

    @Size(min = 20, max = 2000, message = "Annotation must be between 20 and 2000 characters")
    private String annotation;

    @Size(min = 20, max = 7000, message = "Description must be between 20 and 7000 characters")
    private String description;

    private Long category;

    private LocationDto location;

    private String eventDate;

    private Boolean paid;

    @PositiveOrZero(message = "Participant limit must be 0 or greater")
    private Integer participantLimit;

    private Boolean requestModeration;

    @Pattern(
            regexp = "^(SEND_TO_REVIEW|CANCEL_REVIEW)$",
            message = "State action must be SEND_TO_REVIEW or CANCEL_REVIEW"
    )
    private String stateAction;
}