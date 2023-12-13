package org.flickit.assessment.core.adapter.in.rest.exception;

import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.exception.api.ErrorResponseDto;
import org.flickit.assessment.core.application.exception.CalculateNotValidException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.flickit.assessment.core.adapter.in.rest.exception.api.ErrorCodes.CALCULATE_NOT_VALID;

@RestControllerAdvice
public class CalculateNotValidExceptionHandler {

    @ResponseBody
    @ExceptionHandler(CalculateNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ErrorResponseDto handle(CalculateNotValidException ex) {
        return new ErrorResponseDto(CALCULATE_NOT_VALID, MessageBundle.message(ex.getMessage()));
    }
}


