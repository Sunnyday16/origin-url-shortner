package org.example.urlshortener.util;

import com.google.common.hash.Hashing;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class ShortCodeGeneratorTest {

    @Test
    @DisplayName("generateHashShortCode returns exactly 8 characters")
    void generateHashShortCode_lengthIsEight() {
        String originalUrl = "https://www.example.com";
        String code = ShortCodeGenerator.generateHashShortCode(originalUrl);
        assertNotNull(code, "Short code should not be null");
        assertEquals(8, code.length(), "Short code must be 8 characters long");
    }

    @Test
    @DisplayName("generateHashShortCode is deterministic for same input")
    void generateHashShortCode_isDeterministic() {
        String originalUrl = "https://www.example.com/page";
        String first = ShortCodeGenerator.generateHashShortCode(originalUrl);
        String second = ShortCodeGenerator.generateHashShortCode(originalUrl);
        assertEquals(first, second, "Short codes for the same URL should be identical");
    }

    @Test
    @DisplayName("generateHashShortCode produces correct prefix of full hash")
    void generateHashShortCode_prefixMatchesHash() {
        String originalUrl = "https://www.example.com/test";
        String fullHash = Hashing.murmur3_32()
                .hashString(originalUrl, StandardCharsets.UTF_8)
                .toString();
        String expectedPrefix = fullHash.substring(0, 8);
        String code = ShortCodeGenerator.generateHashShortCode(originalUrl);
        assertEquals(expectedPrefix, code, "Short code should be the first 8 chars of the full hash");
    }

    @Test
    @DisplayName("generateHashShortCode produces different codes for different inputs")
    void generateHashShortCode_differentUrlsProduceDifferentCodes() {
        String url1 = "https://siteA.com";
        String url2 = "https://siteB.com";
        String code1 = ShortCodeGenerator.generateHashShortCode(url1);
        String code2 = ShortCodeGenerator.generateHashShortCode(url2);
        assertNotNull(code1);
        assertNotNull(code2);
        assertNotEquals(code1, code2, "Different URLs should produce different short codes");
    }
}