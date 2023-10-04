package org.flickit.flickitassessmentcore.adapter.in.rest.exception;

import org.flickit.flickitassessmentcore.adapter.in.rest.exception.api.ErrorResponseDto;
import org.flickit.flickitassessmentcore.application.service.exception.AssessmentsNotComparableException.AssessmentsNotComparableException;
import org.flickit.flickitassessmentcore.common.MessageBundle;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.flickit.flickitassessmentcore.adapter.in.rest.exception.api.ErrorCodes.ASSESSMENTS_NOT_COMPARABLE;

@RestControllerAdvice
public class AssessmentsNotComparableExceptionHandler {

    @ResponseBody
    @ExceptionHandler(AssessmentsNotComparableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ErrorResponseDto handle(AssessmentsNotComparableException ex) {
        return new ErrorResponseDto(ASSESSMENTS_NOT_COMPARABLE, MessageBundle.message(ex.getMessage()));
    }
}
