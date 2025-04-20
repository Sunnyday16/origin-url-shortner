package org.example.urlshortener.controller;


import org.example.urlshortener.model.ShortenRequest;
import org.example.urlshortener.model.ShortenResponse;
import org.example.urlshortener.model.UrlMappingResponse;
import org.example.urlshortener.service.URLShortenerService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;

@RestController
@Validated
public class URLShortenerController {

    private final URLShortenerService urlShortenerService;

    public URLShortenerController(URLShortenerService urlShortenerService) {
        this.urlShortenerService = urlShortenerService;
    }

    @PostMapping("/shorten")
    @ResponseStatus(HttpStatus.CREATED)
    public ShortenResponse createShortUrl(@Valid @RequestBody ShortenRequest request) {
        return urlShortenerService.shortenUrl(request.originalUrl());
    }

    @GetMapping("/{shortCode}")
    public void redirectToOriginalUrl(@PathVariable String shortCode, HttpServletResponse response) throws IOException {
        response.sendRedirect(urlShortenerService.getOriginalUrl(shortCode));
    }

    @GetMapping("/info/{shortCode}")
    public UrlMappingResponse getUrlMapping(@PathVariable String shortCode) {
        return urlShortenerService.getUrlMapping(shortCode);
    }
}
