package org.flickit.assessment.common.exception.handler;

import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.exception.DeprecatedVersionException;
import org.flickit.assessment.common.exception.api.ErrorResponseDto;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.flickit.assessment.common.exception.api.ErrorCodes.DEPRECATED;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DeprecatedVersionExceptionHandler {

    @ResponseBody
    @ExceptionHandler(DeprecatedVersionException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ErrorResponseDto handle(DeprecatedVersionException ex) {
        return new ErrorResponseDto(DEPRECATED, MessageBundle.message(ex.getMessage()));
    }
}
