package org.flickit.assessment.common.exception.handler;

import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.exception.MissingAuthorizationHeaderException;
import org.flickit.assessment.common.exception.api.ErrorResponseDto;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.flickit.assessment.common.exception.api.ErrorCodes.UNAUTHORIZED;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MissingAuthorizationHeaderExceptionHandler {

    @ResponseBody
    @ExceptionHandler(MissingAuthorizationHeaderException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    ErrorResponseDto handle(MissingAuthorizationHeaderException ex) {
        return new ErrorResponseDto(UNAUTHORIZED, MessageBundle.message(ex.getMessage()));
    }
}
