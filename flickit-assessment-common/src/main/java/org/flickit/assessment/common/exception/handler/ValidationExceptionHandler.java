package org.flickit.assessment.common.exception.handler;

import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.common.exception.api.ErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.flickit.assessment.common.exception.api.ErrorCodes.INVALID_INPUT;

@RestControllerAdvice
public class ValidationExceptionHandler {

    @ResponseBody
    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ErrorResponseDto handle(ValidationException ex) {
        return new ErrorResponseDto(INVALID_INPUT, MessageBundle.message(ex.getMessage()));
    }
}
