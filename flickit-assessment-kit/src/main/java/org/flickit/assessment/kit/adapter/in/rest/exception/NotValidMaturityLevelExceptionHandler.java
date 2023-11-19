package org.flickit.assessment.kit.adapter.in.rest.exception;

import org.flickit.assessment.kit.adapter.in.rest.exception.api.ErrorResponseDto;
import org.flickit.assessment.kit.application.exception.NotValidMaturityLevelException;
import org.flickit.assessment.kit.common.MessageBundle;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.flickit.assessment.kit.adapter.in.rest.exception.api.ErrorCodes.INVALID_MATURITY_LEVEL;

@RestControllerAdvice
public class NotValidMaturityLevelExceptionHandler {

    @ResponseBody
    @ExceptionHandler(NotValidMaturityLevelException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ErrorResponseDto handle(NotValidMaturityLevelException ex) {
        return new ErrorResponseDto(INVALID_MATURITY_LEVEL, MessageBundle.message(ex.getMessage()));
    }
}
