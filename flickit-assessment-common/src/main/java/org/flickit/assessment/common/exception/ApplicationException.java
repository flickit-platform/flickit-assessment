package org.flickit.assessment.common.exception;

import lombok.Getter;
import org.flickit.assessment.common.application.MessageBundle;

import java.util.Locale;

@Getter
public abstract class ApplicationException extends RuntimeException {

    private final String messageKey;
    private final Object[] messageArgs;

    public ApplicationException(String messageKey, Object... messageArgs) {
        this.messageKey = messageKey;
        this.messageArgs = messageArgs;
    }

    @Override
    public String getMessage() {
        return MessageBundle.message(messageKey, messageArgs);
    }

    public String getLocaleMessage(Locale locale) {
        return MessageBundle.message(messageKey, locale, messageArgs);
    }
}
