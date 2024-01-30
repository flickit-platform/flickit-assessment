package org.flickit.assessment.kit.adapter.out.rest.exception;

import lombok.Getter;

@Getter
public class DSLSyntaxErrorException extends RuntimeException {

    private final String syntaxError;

    public DSLSyntaxErrorException(Throwable cause, String syntaxError) {
        super(cause);
        this.syntaxError = syntaxError;
    }
}
