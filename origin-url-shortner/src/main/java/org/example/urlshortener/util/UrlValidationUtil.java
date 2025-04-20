package org.example.urlshortener.util;


import org.example.urlshortener.exception.enumeration.UrlShortenerError;
import org.example.urlshortener.exception.handler.URLShortenerServiceException;
import org.springframework.http.HttpStatus;

import java.net.URI;
import java.net.URISyntaxException;

public final class UrlValidationUtil {

    private UrlValidationUtil() {
    }

    public static void validate(String urlStr) {
        try {
            URI uri = new URI(urlStr);
            String scheme = uri.getScheme();
            String host   = uri.getHost();

            if (scheme == null || host == null) {
                throw new URLShortenerServiceException(HttpStatus.BAD_REQUEST,
                    UrlShortenerError.INVALID_URL_FORMAT.getMessage() + " - " + urlStr
                );
            }
        } catch (URISyntaxException e) {
            throw new URLShortenerServiceException(HttpStatus.BAD_REQUEST,
                UrlShortenerError.INVALID_URL_FORMAT.getMessage() + " - " + urlStr
            );
        }
    }
}
