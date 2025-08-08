package ru.practicum.exception;

public class FallbackResponse extends RuntimeException {
    public FallbackResponse(String message) {
        super(message);
    }
}