package org.flickit.flickitassessmentcore.adapter.in.rest.assessment;

import org.flickit.flickitassessmentcore.application.service.assessment.AssessmentColorNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AssessmentColorNotFoundAdvice {
    @ResponseBody
    @ExceptionHandler(AssessmentColorNotFoundException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    String assessmentColorNotFoundHandler(AssessmentColorNotFoundException ex) {
        return ex.getMessage();
    }
}
