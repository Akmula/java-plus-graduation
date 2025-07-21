package ru.practicum.ewm.main.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {
    private long id;

    @Size(min = 1, max = 50, message = "Название категории должно быть от 1 до 50 символов!")
    @NotBlank(message = "Название категории не может быть пустым!")
    private String name;
}