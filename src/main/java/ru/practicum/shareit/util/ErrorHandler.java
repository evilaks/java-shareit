package ru.practicum.shareit.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.util.exception.*;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequest(final BadRequestException e) {
        log.warn("BadRequest exception caught");
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(final NotFoundException e) {
        log.warn("NotFound exception caught");
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final ValidationException e) {
        log.warn("Validation exception caught");
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConflictException(final ConflictException e) {
        log.warn("Conflict exception caught");
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(NotAllowedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleNotAllowedException(final NotAllowedException e) {
        log.warn("NotAllowed exception caught");
        return new ErrorResponse(e.getMessage());
    }

    @Getter
    @AllArgsConstructor
    public static class ErrorResponse {
        private String error;
    }
}
