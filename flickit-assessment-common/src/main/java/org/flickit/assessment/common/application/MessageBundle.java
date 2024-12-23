package org.flickit.assessment.common.application;

import lombok.experimental.UtilityClass;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.util.Locale;

@UtilityClass
public class MessageBundle {

    private static final MessageSource messageSource = messageSource();

    public static String message(String key, Object... args) {
        return message(key, LocaleContextHolder.getLocale(), args);
    }

    private static String message(String key, Locale locale, Object... args) {
		return messageSource.getMessage(key, args, locale);
	}

    private static MessageSource messageSource() {
        var messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames("i18n/core/messages", "i18n/kit/messages", "i18n/advice/messages",
            "i18n/users/messages", "i18n/messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
}
