package ru.practicum.ewm.main.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCommentDto {

    @NotBlank
    @Size(max = 500)
    private String text;
}
