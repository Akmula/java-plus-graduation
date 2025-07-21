package ru.practicum.ewm.main.dto;

import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.UniqueElements;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCompilationRequest {
    @Size(min = 1, max = 50)
    private String title;
    @UniqueElements
    private List<Long> events;
    private Boolean pinned;
}