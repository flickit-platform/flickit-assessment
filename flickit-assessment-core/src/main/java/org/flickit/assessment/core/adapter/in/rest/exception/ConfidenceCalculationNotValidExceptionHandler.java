package org.flickit.assessment.core.adapter.in.rest.exception;

import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.exception.api.ErrorResponseDto;
import org.flickit.assessment.core.application.exception.ConfidenceCalculationNotValidException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.flickit.assessment.core.adapter.in.rest.exception.api.ErrorCodes.CONFIDENCE_CALCULATION_NOT_VALID;

@RestControllerAdvice
public class ConfidenceCalculationNotValidExceptionHandler {

    @ResponseBody
    @ExceptionHandler(ConfidenceCalculationNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ErrorResponseDto handle(ConfidenceCalculationNotValidException ex) {
        return new ErrorResponseDto(CONFIDENCE_CALCULATION_NOT_VALID, MessageBundle.message(ex.getMessage()));
    }
}
