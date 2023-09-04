package org.flickit.flickitassessmentcore.adapter.in.rest.exception;

import org.flickit.flickitassessmentcore.adapter.in.rest.exception.api.ErrorResponseDto;
import org.flickit.flickitassessmentcore.application.service.exception.CalculateNotValidException;
import org.flickit.flickitassessmentcore.common.MessageBundle;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.flickit.flickitassessmentcore.adapter.in.rest.exception.api.ErrorCodes.CALCULATE_NOT_VALID;

@RestControllerAdvice
public class CalculateNotValidExceptionHandler {

    @ResponseBody
    @ExceptionHandler(CalculateNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ErrorResponseDto handle(CalculateNotValidException ex) {
        return new ErrorResponseDto(CALCULATE_NOT_VALID, MessageBundle.message(ex.getMessage()));
    }
}
