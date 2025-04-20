package org.example.urlshortener.util;

import org.example.urlshortener.exception.enumeration.UrlShortenerError;
import org.example.urlshortener.exception.handler.URLShortenerServiceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class UrlValidationUtilTest {

    @Test
    @DisplayName("validate should pass for a well-formed HTTP URL")
    void validate_validHttpUrl_noException() {
        String url = "http://www.example.com/path?query=1";
        assertDoesNotThrow(() -> UrlValidationUtil.validate(url));
    }

    @Test
    @DisplayName("validate should pass for a well-formed HTTPS URL")
    void validate_validHttpsUrl_noException() {
        String url = "https://originenergy.com.au";
        assertDoesNotThrow(() -> UrlValidationUtil.validate(url));
    }

    @Test
    @DisplayName("validate should throw for URL with missing scheme")
    void validate_missingScheme_throwsException() {
        String url = "www.example.com/path";
        assertThatThrownBy(() -> UrlValidationUtil.validate(url))
                .isInstanceOf(URLShortenerServiceException.class)
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("validate should throw for URL with no host")
    void validate_noHost_throwsException() {
        String url = "http:///nohost";
        assertThatThrownBy(() -> UrlValidationUtil.validate(url))
                .isInstanceOf(URLShortenerServiceException.class)
                .matches(ex -> ((URLShortenerServiceException) ex)
                        .getMessage().contains(UrlShortenerError.INVALID_URL_FORMAT.getMessage()));
    }

    @Test
    @DisplayName("validate should throw for completely malformed URL")
    void validate_malformedUrl_throwsException() {
        String url = "://bad!url";
        assertThatThrownBy(() -> UrlValidationUtil.validate(url))
                .isInstanceOf(URLShortenerServiceException.class)
                .satisfies(ex -> {
                    URLShortenerServiceException use = (URLShortenerServiceException) ex;
                    assertThat(use.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
                    assertThat(use.getMessage()).contains(UrlShortenerError.INVALID_URL_FORMAT.getMessage());
                    assertThat(use.getMessage()).contains(url);
                });
    }
}
