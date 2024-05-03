package org.flickit.assessment.common.exception.handler;

import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.exception.CalculateNotValidException;
import org.flickit.assessment.common.exception.api.ErrorResponseDto;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.flickit.assessment.common.exception.api.ErrorCodes.CALCULATE_NOT_VALID;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CalculateNotValidExceptionHandler {

    @ResponseBody
    @ExceptionHandler(CalculateNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ErrorResponseDto handle(CalculateNotValidException ex) {
        return new ErrorResponseDto(CALCULATE_NOT_VALID, MessageBundle.message(ex.getMessage()));
    }
}


