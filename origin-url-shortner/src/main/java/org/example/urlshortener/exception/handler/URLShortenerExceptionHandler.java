package org.example.urlshortener.exception.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class URLShortenerExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(URLShortenerExceptionHandler.class);

    @ExceptionHandler(URLShortenerServiceException.class)
    public ResponseEntity<?> handleUrlShortenerException(URLShortenerServiceException ex) {
        return ResponseEntity
                .status(ex.getStatus())
                .body(new ErrorResponse("url-shortener-error", ex.getStatus().value(), ex.getMessage()));
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining("; "));

        ErrorResponse payload = new ErrorResponse("url-shortener-error", HttpStatus.BAD_REQUEST.value(),errors);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(payload);
    }

}
