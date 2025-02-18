package org.flickit.assessment.common.exception.handler;

import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.exception.api.ErrorResponseDto;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.flickit.assessment.common.exception.api.ErrorCodes.UNSUPPORTED_OPERATION;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class UnsupportedOperationExceptionHandler {

    @ResponseBody
    @ExceptionHandler(UnsupportedOperationException.class)
    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    public ErrorResponseDto handle(UnsupportedOperationException ex) {
        return new ErrorResponseDto(UNSUPPORTED_OPERATION, MessageBundle.message(ex.getMessage()));
    }
}
