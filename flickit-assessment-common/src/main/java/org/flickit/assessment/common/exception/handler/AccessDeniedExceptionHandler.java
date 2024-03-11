package org.flickit.assessment.common.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.api.ErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.flickit.assessment.common.exception.api.ErrorCodes.ACCESS_DENIED;


@Slf4j
@RestControllerAdvice
public class AccessDeniedExceptionHandler {

    @ResponseBody
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    ErrorResponseDto handle(AccessDeniedException ex) {
        log.error(ex.getMessage(), ex);
        return new ErrorResponseDto(ACCESS_DENIED, ex.getMessage());
    }
}
