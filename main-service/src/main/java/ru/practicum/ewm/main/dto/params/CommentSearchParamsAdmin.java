package ru.practicum.ewm.main.dto.params;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommentSearchParamsAdmin {

    private Long authorId;       // фильтр по автору
    private Long eventId;        // фильтр по событию
    private String rangeStart;   // фильтр по дате начала
    private String rangeEnd;     // фильтр по дате конца

    @PositiveOrZero
    private Integer from = PaginationDefaults.DEFAULT_FROM;

    @Positive
    private Integer size = PaginationDefaults.DEFAULT_SIZE;
}