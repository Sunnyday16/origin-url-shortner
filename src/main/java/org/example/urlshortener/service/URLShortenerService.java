package org.example.urlshortener.service;

import org.example.urlshortener.exception.enumeration.UrlShortenerError;
import org.example.urlshortener.exception.handler.URLShortenerServiceException;
import org.example.urlshortener.model.ShortenResponse;
import org.example.urlshortener.model.UrlMappingEntity;
import org.example.urlshortener.model.UrlMappingResponse;
import org.example.urlshortener.repository.UrlMappingRepository;
import org.example.urlshortener.util.ShortCodeGenerator;
import org.example.urlshortener.util.UrlValidationUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class URLShortenerService {

    private final UrlMappingRepository repository;

    @Value("${app.base-url}")
    private String baseUrl;

    public URLShortenerService(UrlMappingRepository repository) {
        this.repository = repository;
    }

    public ShortenResponse shortenUrl(String originalUrl) {
        // 1. Validate
        UrlValidationUtil.validate(originalUrl);

        // 2. Reuse existing mapping
        Optional<UrlMappingEntity> existing = repository.findByOriginalUrl(originalUrl);
        if (existing.isPresent()) {
            return new ShortenResponse(baseUrl + existing.get().getShortCode());
        }

        // 3. Generate a code and resolve collisions
        String shortCode = generateUniqueShortCode(originalUrl);

        // 4. Persist new mapping
        UrlMappingEntity mapping = new UrlMappingEntity(originalUrl, shortCode, LocalDateTime.now());
        repository.save(mapping);

        // 5. Return Shortened URL
        return new ShortenResponse(baseUrl + shortCode);
    }

    public String getOriginalUrl(String shortCode) {
        UrlMappingEntity mapping = repository.findByShortCode(shortCode)
                .orElseThrow(() -> new URLShortenerServiceException(HttpStatus.NOT_FOUND,
                        UrlShortenerError.SHORT_URL_NOT_FOUND.getMessage()));
        return mapping.getOriginalUrl();
    }

    public UrlMappingResponse getUrlMapping(String shortCode) {
        UrlMappingEntity mapping =  repository.findByShortCode(shortCode)
                .orElseThrow(() -> new URLShortenerServiceException(HttpStatus.NOT_FOUND,
                        UrlShortenerError.SHORT_URL_NOT_FOUND.getMessage()));
        return new UrlMappingResponse(
                mapping.getOriginalUrl(),
                baseUrl + mapping.getShortCode(),
                mapping.getCreatedAt()
        );

    }

    private String generateUniqueShortCode(String originalUrl) {
        String shortCode = ShortCodeGenerator.generateHashShortCode(originalUrl);
        int counter = 1;
        while (repository.findByShortCode(shortCode).isPresent()) {
            UrlMappingEntity m = repository.findByShortCode(shortCode).get();
            if (m.getOriginalUrl().equals(originalUrl)) {
                return shortCode;
            }
            shortCode = ShortCodeGenerator.generateHashShortCode(originalUrl + counter++);
        }
        return shortCode;
    }
}