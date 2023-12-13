package org.flickit.assessment.common.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.exception.api.ErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.flickit.assessment.common.exception.api.ErrorCodes.INTERNAL_ERROR;

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
