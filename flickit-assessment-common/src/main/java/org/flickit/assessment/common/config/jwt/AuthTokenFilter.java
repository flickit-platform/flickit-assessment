package org.flickit.assessment.common.config.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Base64;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
@RequiredArgsConstructor
public class AuthTokenFilter extends OncePerRequestFilter {

    private final UserContext context;

    private static final String MDC_USER_ID = "userId";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        try {
            try {
                String jwt = request.getHeader(AUTHORIZATION);
                if (jwt != null) {
                    UserDetail user = getUserFromJwtToken(jwt);
                    context.setUser(user);
                    putLoggerMDC(user);
                }
            } catch (Exception e) {
                logger.error("Cannot set user UserContext", e);
            }
            filterChain.doFilter(request, response);
        } finally {
            removeLoggerMDC();
        }
    }

    private UserDetail getUserFromJwtToken(String jwt) {
        String token = jwt.substring(7);
        String[] chunks = token.split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String payload = new String(decoder.decode(chunks[1]));
        return JwtTranslator.parseJson(payload);
    }

    private static void putLoggerMDC(UserDetail user) {
        if (user != null && user.id() != null)
            MDC.put(MDC_USER_ID, user.id().toString());
    }

    private static void removeLoggerMDC() {
        MDC.remove(MDC_USER_ID);
    }
}
