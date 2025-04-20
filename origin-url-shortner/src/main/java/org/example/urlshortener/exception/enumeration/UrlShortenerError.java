package org.example.urlshortener.exception.enumeration;

public enum UrlShortenerError {
    GENERIC_ERROR("generic.error", "Something went wrong"),
    INVALID_URL_FORMAT("invalid.url.format", "Invalid URL format provided"),
    SHORT_URL_NOT_FOUND("short.url.not.found", "Short URL not found");

    private final String code;
    private final String message;

    UrlShortenerError(final String code, final String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
