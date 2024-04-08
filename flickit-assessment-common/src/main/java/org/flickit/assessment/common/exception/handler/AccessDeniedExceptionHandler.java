package org.flickit.assessment.common.exception.handler;

import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.api.ErrorResponseDto;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.flickit.assessment.common.exception.api.ErrorCodes.ACCESS_DENIED;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AccessDeniedExceptionHandler {

    @ResponseBody
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    ErrorResponseDto handle(AccessDeniedException ex) {
        return new ErrorResponseDto(ACCESS_DENIED, MessageBundle.message(ex.getMessage()));
    }
}
