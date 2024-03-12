package org.flickit.assessment.advice.application.exception;

public class FinalSolutionNotFoundException extends RuntimeException {

    public FinalSolutionNotFoundException( String message, Throwable cause) {
        super(message, cause);
    }
}
