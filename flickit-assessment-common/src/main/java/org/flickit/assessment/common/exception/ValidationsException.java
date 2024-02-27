package org.flickit.assessment.common.exception;

import lombok.Getter;
import org.flickit.assessment.common.exception.api.Notification;

@Getter
public class ValidationsException extends RuntimeException {

    private final transient String code;
    private final transient Notification validation;

    public ValidationsException(String code, Notification validation) {
        this.code = code;
        this.validation = validation;
    }
}
