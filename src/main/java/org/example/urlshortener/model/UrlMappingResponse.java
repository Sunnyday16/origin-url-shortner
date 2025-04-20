package org.example.urlshortener.model;

import java.time.LocalDateTime;

public record UrlMappingResponse(
    String originalUrl,
    String shortUrl,
    LocalDateTime createdAt
) {}
