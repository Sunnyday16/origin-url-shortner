package org.example.urlshortener.controller;


import org.example.urlshortener.exception.enumeration.UrlShortenerError;
import org.example.urlshortener.exception.handler.URLShortenerServiceException;
import org.example.urlshortener.model.ShortenResponse;
import org.example.urlshortener.model.UrlMappingResponse;
import org.example.urlshortener.service.URLShortenerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = URLShortenerController.class)
class URLShortenerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private URLShortenerService urlShortenerService;

    private final String BASE = "http://short.ly/";

    @Test
    @DisplayName("POST /shorten → 201 + { shortUrl } when input is valid")
    void shorten_validInput_returnsShortUrl() throws Exception {
        String inputUrl = "https://www.originenergy.com.au/plans.html";
        String expected = "http://short.ly/abc12345";

        when(urlShortenerService.shortenUrl(inputUrl))
                .thenReturn(new ShortenResponse(expected));

        mockMvc.perform(post("/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"originalUrl\":\"" + inputUrl + "\"}"))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.shortUrl").value(expected));
    }

    @Test
    @DisplayName("POST /shorten → 400 when URL format is invalid")
    void shorten_invalidUrl_returns400() throws Exception {
        when(urlShortenerService.shortenUrl("not-a-url"))
                .thenThrow(new URLShortenerServiceException(HttpStatus.BAD_REQUEST,"Invalid URL format provided - not-a-url"));

        mockMvc.perform(post("/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"originalUrl\":\"not-a-url\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Invalid URL format")));
    }

    @Test
    @DisplayName("GET /{code} → 302 redirect to original URL when code exists")
    void redirect_existingCode_redirects() throws Exception {
        String code = "abc12345";
        String original = "https://www.originenergy.com.au/plans.html";

       when(urlShortenerService.getOriginalUrl(code))
                .thenReturn(original);

        mockMvc.perform(get("/" + code))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(original));
    }

    @Test
    @DisplayName("GET /{code} → 404 when code not found")
    void redirect_unknownCode_returns404() throws Exception {
        when(urlShortenerService.getOriginalUrl(anyString()))
                .thenThrow(new URLShortenerServiceException(HttpStatus.NOT_FOUND,"Short URL not found"));

        mockMvc.perform(get("/doesNotExist"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("not found")));
    }

    @Test
    @DisplayName(" Opt: GET /info/{shortCode} → 200 and correct body when mapping exists")
    void getInfo_existingMapping_returns200() throws Exception {
        String code = "abc12345";
        String original = "https://example.com/page";
        LocalDateTime createdAt = LocalDateTime.of(2025, 4, 19, 20, 0, 0);

        when(urlShortenerService.getUrlMapping(code))
                .thenReturn(new UrlMappingResponse(original, BASE + code, createdAt));
        String expectedTimestamp = createdAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        mockMvc.perform(get("/info/{shortCode}", code))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.originalUrl").value(original))
                .andExpect(jsonPath("$.shortUrl").value(BASE + code))
                .andExpect(jsonPath("$.createdAt").value(expectedTimestamp));
    }

    @Test
    @DisplayName("Opt: GET /info/{shortCode} → 404 when mapping not found")
    void getInfo_missingMapping_returns404() throws Exception {
        String code = "doesNotExist";

        when(urlShortenerService.getUrlMapping(code))
                .thenThrow(new URLShortenerServiceException(
                        HttpStatus.NOT_FOUND,
                        UrlShortenerError.SHORT_URL_NOT_FOUND.getMessage()
                ));

        mockMvc.perform(get("/info/{shortCode}", code))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value(UrlShortenerError.SHORT_URL_NOT_FOUND.getMessage()));
    }
}
