package org.flickit.assessment.common.exception;

import lombok.Getter;

@Getter
public class ValidationException extends ApplicationException {

    public ValidationException(String messageKey, Object... messageArgs) {
        super(messageKey, messageArgs);
    }
}
