package org.flickit.assessment.common.exception.handler;

import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.exception.ResourceAlreadyExistException;
import org.flickit.assessment.common.exception.api.ErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.flickit.assessment.common.exception.api.ErrorCodes.ALREADY_EXIST;

@RestControllerAdvice
public class ResourceAlreadyExistExceptionHandler {

    @ResponseBody
    @ExceptionHandler(ResourceAlreadyExistException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    ErrorResponseDto handle(ResourceAlreadyExistException ex) {
        return new ErrorResponseDto(ALREADY_EXIST, MessageBundle.message(ex.getMessage()));
    }
}
