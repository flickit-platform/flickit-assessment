package org.flickit.assessment.kit.adapter.in.rest.exception;

import org.flickit.assessment.kit.adapter.in.rest.exception.api.ErrorResponseDto;
import org.flickit.assessment.kit.application.exception.InvalidActionException;
import org.flickit.assessment.kit.common.MessageBundle;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.flickit.assessment.kit.adapter.in.rest.exception.api.ErrorCodes.INVALID_ACTION;

@RestControllerAdvice
public class InvalidActionExceptionHandler {

    @ResponseBody
    @ExceptionHandler(InvalidActionException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ErrorResponseDto handle(InvalidActionException ex) {
        return new ErrorResponseDto(INVALID_ACTION, MessageBundle.message(ex.getMessage()));
    }
}
