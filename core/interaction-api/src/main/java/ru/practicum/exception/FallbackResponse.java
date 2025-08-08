package ru.practicum.exception;

public class FallbackResponse extends RuntimeException {
    private static final String MSG_TEMPLATE = "Warehouse - сервис временно недоступен";

    public FallbackResponse(String message) {
        super(message);
    }
}