package org.flickit.assessment.common.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.exception.InvalidContentException;
import org.flickit.assessment.common.exception.api.ErrorResponseDto;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.flickit.assessment.common.exception.api.ErrorCodes.INVALID_CONTENT;

@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class InvalidContentExceptionHandler {

    @ResponseBody
    @ExceptionHandler(InvalidContentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ErrorResponseDto handle(InvalidContentException ex) {
        log.error("Invalid content error", ex);
        return new ErrorResponseDto(INVALID_CONTENT, MessageBundle.message(ex.getMessage()));
    }
}
