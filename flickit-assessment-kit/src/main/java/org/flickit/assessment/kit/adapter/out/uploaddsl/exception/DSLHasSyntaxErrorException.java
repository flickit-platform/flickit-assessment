package org.flickit.assessment.kit.adapter.out.uploaddsl.exception;

import lombok.Getter;

@Getter
public class DSLHasSyntaxErrorException extends RuntimeException {

    public DSLHasSyntaxErrorException(String message) {
        super(message);
    }
}
