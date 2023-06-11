package org.flickit.flickitassessmentcore.application.service.exception;

import lombok.Getter;

public class UniqueConstraintViolationException extends RuntimeException {

    @Getter
    private final String field;

    public UniqueConstraintViolationException(String field, String message) {
        super(message);
        this.field = field;
    }
}
