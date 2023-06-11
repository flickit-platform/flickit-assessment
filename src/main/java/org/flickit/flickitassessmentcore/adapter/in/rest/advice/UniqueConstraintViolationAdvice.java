package org.flickit.flickitassessmentcore.adapter.in.rest.advice;

import org.flickit.flickitassessmentcore.adapter.in.rest.advice.common.ValidationError;
import org.flickit.flickitassessmentcore.application.service.exception.UniqueConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.flickit.flickitassessmentcore.adapter.in.rest.advice.common.ErrorBundleLoader.getErrorMessage;

@RestControllerAdvice
public class UniqueConstraintViolationAdvice {

    @ResponseBody
    @ExceptionHandler(UniqueConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationError handleConstraintViolation(UniqueConstraintViolationException ex) {
        return buildValidationErrors(ex);
    }

    private ValidationError buildValidationErrors(UniqueConstraintViolationException ex) {
        return new ValidationError(ex.getField(), getErrorMessage(ex.getMessage()));
    }
}
