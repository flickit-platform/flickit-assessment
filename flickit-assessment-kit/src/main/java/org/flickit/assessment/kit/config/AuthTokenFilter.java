package org.flickit.assessment.kit.config;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AuthTokenFilter extends OncePerRequestFilter {

    @Value("${app.keycloak.jwt-secret}")
    private String jwtSecret;

    private final SecurityContext context;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        try {
            SecretKey key = new SecretKeySpec(jwtSecret.getBytes(), "HmacSHA256");
            String jwt = request.getHeader("JWT");

            if (jwt != null && validateJwtToken(jwt, key)) {
                String username = getUserNameFromJwtToken(jwt, key);
                context.setUsername(username);
            }
        } catch (Exception e) {
            logger.error("Cannot set user username: {}", e);
        }

        filterChain.doFilter(request, response);
    }

    public boolean validateJwtToken(String authToken, SecretKey key) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(authToken);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e);
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e);
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e);
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e);
        }
        return false;
    }

    public String getUserNameFromJwtToken(String token, SecretKey key) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().get("email", String.class);
    }
}
