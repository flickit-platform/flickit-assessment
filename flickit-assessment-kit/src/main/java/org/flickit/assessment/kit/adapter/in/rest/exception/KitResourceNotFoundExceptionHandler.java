package org.flickit.assessment.kit.adapter.in.rest.exception;

import org.flickit.assessment.kit.adapter.in.rest.exception.api.ErrorResponseDto;
import org.flickit.assessment.kit.application.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.common.MessageBundle;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.flickit.assessment.kit.adapter.in.rest.exception.api.ErrorCodes.INVALID_DSL_CONTENT;

@RestControllerAdvice
public class KitResourceNotFoundExceptionHandler {

    @ResponseBody
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ErrorResponseDto handle(ResourceNotFoundException ex) {
        return new ErrorResponseDto(INVALID_DSL_CONTENT, MessageBundle.message(ex.getMessage()));
    }
}
