package org.flickit.assessment.common.config.locale;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.LocaleResolver;

import java.util.Locale;

@Component
public class UserLocaleContext {

    private static LocaleResolver localeResolver;

    public UserLocaleContext(LocaleResolver localeResolver) {
        UserLocaleContext.localeResolver = localeResolver;
    }

    public static Locale getLocale() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null)
            throw new IllegalStateException("No request attributes are available. Cannot resolve locale.");

        return localeResolver.resolveLocale(attributes.getRequest());
    }
}
