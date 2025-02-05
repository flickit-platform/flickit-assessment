package org.flickit.assessment.common.exception;

import lombok.Getter;

@Getter
public class InvalidStateException extends RuntimeException {

    private final String code;

    public InvalidStateException(String code, String message) {
        super(message);
        this.code = code;
    }
}
