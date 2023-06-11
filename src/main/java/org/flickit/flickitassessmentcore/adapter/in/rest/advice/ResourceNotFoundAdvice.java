package org.flickit.flickitassessmentcore.adapter.in.rest.advice;

import org.flickit.flickitassessmentcore.application.service.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.flickit.flickitassessmentcore.adapter.in.rest.advice.common.ErrorBundleLoader.getErrorMessage;

@RestControllerAdvice
public class ResourceNotFoundAdvice {
    @ResponseBody
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String assessmentColorNotFoundHandler(ResourceNotFoundException ex) {
        return getErrorMessage(ex.getMessage());
    }
}
