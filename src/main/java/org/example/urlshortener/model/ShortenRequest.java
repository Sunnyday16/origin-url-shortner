package org.example.urlshortener.model;

import javax.validation.constraints.NotBlank;

public record ShortenRequest(

    @NotBlank
    String originalUrl) {

}
