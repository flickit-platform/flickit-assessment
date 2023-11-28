package org.flickit.assessment.core.adapter.in.rest.exception;

import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.core.adapter.in.rest.exception.api.ErrorResponseDto;
import org.flickit.assessment.core.common.MessageBundle;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.flickit.assessment.core.adapter.in.rest.exception.api.ErrorCodes.INTERNAL_ERROR;

@Slf4j
//@RestControllerAdvice
public class RuntimeExceptionHandler {

    @ResponseBody
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    ErrorResponseDto handle(RuntimeException ex) {
        log.error("An unexpected error occurred", ex);
        return new ErrorResponseDto(INTERNAL_ERROR, MessageBundle.message("common.internal.error"));
    }
}
