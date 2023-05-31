package org.flickit.flickitassessmentcore.application.service.assessment;

public class ResourceNotFoundException extends RuntimeException{
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
