package org.example.urlshortener.util;

import com.google.common.hash.Hashing;
import java.nio.charset.StandardCharsets;

public class ShortCodeGenerator {

    public static String generateHashShortCode(String originalUrl) {
        String hash = Hashing.murmur3_32()
                             .hashString(originalUrl, StandardCharsets.UTF_8)
                             .toString();
        return hash.substring(0, 8);
    }
}
