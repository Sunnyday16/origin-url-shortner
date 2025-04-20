package org.example.urlshortener.config;

import org.example.urlshortener.repository.UrlMappingRepository;
import org.example.urlshortener.service.URLShortenerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration

public class ServiceConfig {


    @Bean
    public URLShortenerService urlShortenerService(final UrlMappingRepository urlMappingRepository) {
        return new URLShortenerService(urlMappingRepository);
    }

}
