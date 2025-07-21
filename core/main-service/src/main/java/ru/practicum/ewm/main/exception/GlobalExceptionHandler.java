package ru.practicum.ewm.main.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Ошибки валидации @Valid
    @ExceptionHandler({ValidationException.class, MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handlerValidationException(final ValidationException e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String stackTrace = sw.toString();
        log.error("Ошибка: 400 BAD_REQUEST - {}", stackTrace);
        return new ApiError("Запрос составлен некорректно", e.getMessage(),
                HttpStatus.BAD_REQUEST.name(), LocalDateTime.now());
    }

    // Нарушения ограничений @NotNull, @PositiveOrZero и др.
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // Бизнес-исключения (ручные throw new IllegalArgumentException(...))
    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class, NoSuchElementException.class})
    public ResponseEntity<Map<String, String>> handleIllegalState(RuntimeException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // Отсутствие обязательного параметра запроса ?eventId=
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, String>> handleMissingParam(MissingServletRequestParameterException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", String.format("Missing required parameter: %s", ex.getParameterName()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> handleResponseStatus(ResponseStatusException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getReason()); // например: "Event is not published"
        return ResponseEntity.status(ex.getStatusCode()).body(error);
    }

    // NotFoundException
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handlerNotFoundException(final NotFoundException e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String stackTrace = sw.toString();
        log.error("Ошибка: 404 NOT_FOUND - {}", stackTrace);
        return new ApiError("Объект не найден или недоступен", e.getMessage(),
                HttpStatus.NOT_FOUND.name(), LocalDateTime.now());
    }

    // Дублирование данных (например, попытка создать категорию с уже существующим названием)
    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleDuplicatedData(final ConflictException e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String stackTrace = sw.toString();
        log.error("Ошибка: 409 CONFLICT - {}", stackTrace);
        return new ApiError("Дублирование информации", e.getMessage(),
                HttpStatus.CONFLICT.name(), LocalDateTime.now());
    }

    // Общий обработчик (fallback)
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handlerException(final Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String stackTrace = sw.toString();
        log.error("Ошибка: 500 INTERNAL_SERVER_ERROR - {}", stackTrace);
        return new ApiError("Неизвестная ошибка", e.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.name(), LocalDateTime.now());
    }
}