package ru.practicum.ewm.server.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
public class ApiError {
    private final HttpStatus status;
    private final String error;
    private final String message;
    private final String stackTrace;
}