package org.flickit.assessment.core.adapter.in.rest.exception;

import org.flickit.assessment.core.adapter.in.rest.exception.api.ErrorResponseDto;
import org.flickit.assessment.core.application.exception.NotValidKitContentException;
import org.flickit.assessment.core.common.MessageBundle;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.flickit.assessment.core.adapter.in.rest.exception.api.ErrorCodes.INVALID_INPUT;

@RestControllerAdvice
public class NotValidKitContentExceptionHandler {

    @ResponseBody
    @ExceptionHandler(NotValidKitContentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ErrorResponseDto handle(NotValidKitContentException ex) {
        return new ErrorResponseDto(INVALID_INPUT, MessageBundle.message(ex.getMessage()));
    }
}
