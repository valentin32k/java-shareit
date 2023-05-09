package ru.practicum.shareit.exceptions;

public class BadMethodArgumentsException extends RuntimeException {
    public BadMethodArgumentsException(String message) {
        super(message);
    }
}
