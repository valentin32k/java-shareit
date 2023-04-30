package ru.practicum.shareit.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ExceptionHandlers {
    @ExceptionHandler
    public ResponseEntity<Map<String, String>> badUserException(final RuntimeException e) {
        log.warn(e.getMessage());
        return new ResponseEntity<>(Map.of("error: ", e.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> notFoundException(final NotFoundException e) {
        log.warn(e.getMessage());
        return new ResponseEntity<>(Map.of("error: ", e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> notValidDataException(final MethodArgumentNotValidException e) {
        log.warn(e.getMessage());
        return new ResponseEntity<>(Map.of("error: ", e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> otherExceptionHandler(final Exception e) {
        log.warn(e.getMessage());
        return new ResponseEntity<>(Map.of("error: ", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
