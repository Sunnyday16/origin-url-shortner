package org.example.urlshortener.exception.handler;

import org.springframework.http.HttpStatus;

public class URLShortenerServiceException extends RuntimeException {
    private final HttpStatus status;

    public URLShortenerServiceException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}