package org.flickit.assessment.scenario.fixture.auth;

import io.jsonwebtoken.Jwts;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

import static java.lang.System.currentTimeMillis;

public class JwtTokenTestUtils {

    public static String generateJwtToken(UUID userId) {
        return generateJwtToken(Map.of("sub", userId.toString()));
    }

    public static String generateJwtToken(Map<String, Object> claims) {
        Date expirationDate = new Date(currentTimeMillis() + 3600 * 1000);
        return Jwts.builder()
            .claims().add(claims).and()
            .issuedAt(new Date())
            .expiration(expirationDate)
            .compact();
    }
}
