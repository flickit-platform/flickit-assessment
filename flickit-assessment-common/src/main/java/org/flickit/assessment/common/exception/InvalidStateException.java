package org.flickit.assessment.common.exception;

import lombok.Getter;
import org.flickit.assessment.common.exception.api.ErrorCodes;

@Getter
public class InvalidStateException extends RuntimeException {

    private final String code;

    public InvalidStateException(String code, String message) {
        super(message);
        this.code = code;
    }

    public InvalidStateException(String message) {
        super(message);
        this.code = ErrorCodes.INVALID_STATE;
    }
}
