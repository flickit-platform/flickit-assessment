package org.flickit.assessment.common.config.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Base64;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
@RequiredArgsConstructor
public class AuthTokenFilter extends OncePerRequestFilter {

    private final UserContext context;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        try {
            String jwt = request.getHeader(AUTHORIZATION);

            if (jwt != null) {
                UserDetail user = getUserFromJwtToken(jwt);
                context.setUser(user);
            }
        } catch (Exception e) {
            logger.error("Can not set user UserContext", e);
        }

        filterChain.doFilter(request, response);
    }

    private UserDetail getUserFromJwtToken(String jwt) {
        String token = jwt.substring(7);
        String[] chunks = token.split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String payload = new String(decoder.decode(chunks[1]));
        return JwtTranslator.parseJson(payload);
    }
}
