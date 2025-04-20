package org.example.urlshortener.service;

import org.example.urlshortener.exception.enumeration.UrlShortenerError;
import org.example.urlshortener.exception.handler.URLShortenerServiceException;
import org.example.urlshortener.model.ShortenResponse;
import org.example.urlshortener.model.UrlMappingEntity;
import org.example.urlshortener.model.UrlMappingResponse;
import org.example.urlshortener.repository.UrlMappingRepository;
import org.example.urlshortener.util.ShortCodeGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class URLShortenerServiceTest {

    @Mock
    private UrlMappingRepository repository;

    @InjectMocks
    private URLShortenerService service;

    private static final String BASE_URL = "http://short.ly/";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "baseUrl", BASE_URL);
    }

    @Test
    @DisplayName("shortenUrl should reuse existing mapping if present")
    void shortenUrl_existingMapping() {
        String originalUrl = "https://example.com/page";
        String shortCode = ShortCodeGenerator.generateHashShortCode(originalUrl);
        UrlMappingEntity existingEntity = new UrlMappingEntity(originalUrl, shortCode, LocalDateTime.now());

        when(repository.findByOriginalUrl(originalUrl))
                .thenReturn(Optional.of(existingEntity));

        ShortenResponse response = service.shortenUrl(originalUrl);

        assertThat(response.shortUrl()).isEqualTo(BASE_URL + shortCode);
        verify(repository, never()).save(any(UrlMappingEntity.class));
    }

    @Test
    @DisplayName("shortenUrl should create new mapping when none exists")
    void shortenUrl_newMapping() {
        String originalUrl = "https://example.com/new";
        when(repository.findByOriginalUrl(originalUrl)).thenReturn(Optional.empty());
        String expectedCode = ShortCodeGenerator.generateHashShortCode(originalUrl);
        when(repository.findByShortCode(expectedCode)).thenReturn(Optional.empty());
        doAnswer(inv -> inv.getArgument(0))
                .when(repository).save(any(UrlMappingEntity.class));

        ShortenResponse response = service.shortenUrl(originalUrl);

        assertThat(response.shortUrl()).isEqualTo(BASE_URL + expectedCode);
        verify(repository, times(1)).save(any(UrlMappingEntity.class));
    }

    @Test
    @DisplayName("getOriginalUrl should return original URL when short code exists")
    void getOriginalUrl_found() {
        String shortCode = "abc123";
        String originalUrl = "https://example.com/found";
        UrlMappingEntity entity = new UrlMappingEntity(originalUrl, shortCode, LocalDateTime.now());

        when(repository.findByShortCode(shortCode))
                .thenReturn(Optional.of(entity));

        String result = service.getOriginalUrl(shortCode);

        assertThat(result).isEqualTo(originalUrl);
    }

    @Test
    @DisplayName("getOriginalUrl should throw exception when short code not found")
    void getOriginalUrl_notFound() {
        String shortCode = "missing";
        when(repository.findByShortCode(shortCode)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getOriginalUrl(shortCode))
                .isInstanceOf(URLShortenerServiceException.class)
                .hasMessageContaining(UrlShortenerError.SHORT_URL_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("getUrlMapping should return mapping response when short code exists")
    void getUrlMapping_found() {
        String shortCode = "resp123";
        String originalUrl = "https://example.com/info";
        LocalDateTime createdAt = LocalDateTime.of(2025, 4, 19, 21, 30, 0);
        UrlMappingEntity entity = new UrlMappingEntity(originalUrl, shortCode, createdAt);

        when(repository.findByShortCode(shortCode))
                .thenReturn(Optional.of(entity));

        UrlMappingResponse resp = service.getUrlMapping(shortCode);

        assertThat(resp.originalUrl()).isEqualTo(originalUrl);
        assertThat(resp.shortUrl()).isEqualTo(BASE_URL + shortCode);
        assertThat(resp.createdAt()).isEqualTo(createdAt);
    }

    @Test
    @DisplayName("getUrlMapping should throw exception when short code not found")
    void getUrlMapping_notFound() {
        String shortCode = "none";
        when(repository.findByShortCode(shortCode)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getUrlMapping(shortCode))
                .isInstanceOf(URLShortenerServiceException.class)
                .hasMessageContaining(UrlShortenerError.SHORT_URL_NOT_FOUND.getMessage());
    }
}
