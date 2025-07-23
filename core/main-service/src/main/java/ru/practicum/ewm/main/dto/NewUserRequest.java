package ru.practicum.ewm.main.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewUserRequest {

    @Size(min = 2, max = 250, message = "Имя пользователя должно быть от 2 до 250 символов!")
    @NotBlank(message = "Имя пользователя не может быть пустым!")
    private String name;

    @Email(message = "Email не может быть пустым!")
    @Size(min = 6, max = 254, message = "Email должен быть от 6 до 254 символов!")
    @NotBlank(message = "Email не может быть пустым!")
    private String email;
}