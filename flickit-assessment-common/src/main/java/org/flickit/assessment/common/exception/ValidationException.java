package org.flickit.assessment.common.exception;

import lombok.Getter;
import org.flickit.assessment.common.exception.api.Notification;

@Getter
public class ValidationException extends RuntimeException {

    private final String code;
    private final Notification validation;

    public ValidationException(String code, Notification validation) {
        this.code = code;
        this.validation = validation;
    }

    public ValidationException(String code, String errorMsg) {
        this.code = code;
        this.validation = new Notification().add(errorMsg);
    }
}
