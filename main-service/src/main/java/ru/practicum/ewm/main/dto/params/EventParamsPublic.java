package ru.practicum.ewm.main.dto.params;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventParamsPublic {

    private String text; // текст для поиска в содержимом аннотации и подробном описании события

    private List<Long> categories; // список идентификаторов категорий в которых будет вестись поиск

    private Boolean paid; // поиск только платных/бесплатных событий

    private String rangeStart; // дата и время не раньше которых должно произойти событие

    private String rangeEnd; // дата и время не позже которых должно произойти событие

    private boolean onlyAvailable = false; // только события у которых не исчерпан лимит запросов на участие

    private String sort; // Вариант сортировки: по дате события или по количеству просмотров

    @PositiveOrZero
    private Integer from = PaginationDefaults.DEFAULT_FROM;  // количество событий, которые нужно пропустить // Default value : 0

    @Positive
    private Integer size = PaginationDefaults.DEFAULT_SIZE;  // количество событий в наборе // Default value : 10

}