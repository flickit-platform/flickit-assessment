package org.flickit.assessment.common.exception.handler;

import org.flickit.assessment.common.exception.InvalidStateException;
import org.flickit.assessment.common.exception.api.ErrorResponseDto;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class InvalidStateExceptionHandler {

    @ResponseBody
    @ExceptionHandler(InvalidStateException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    ErrorResponseDto handle(InvalidStateException ex) {
        return new ErrorResponseDto(ex.getCode(), ex.getMessage());
    }
}
