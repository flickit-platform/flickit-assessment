package org.flickit.assessment.common.exception.handler;

import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.common.exception.api.ErrorResponsesDto;
import org.flickit.assessment.common.exception.api.Notification;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class ValidationExceptionHandler {

    private static final short MAX_ERROR_NUMBER = 10;

    @ResponseBody
    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponsesDto handle(ValidationException ex) {
        return new ErrorResponsesDto(ex.getCode(), collectMsgList(ex));
    }

    private List<String> collectMsgList(ValidationException ex) {
        return ex.getValidation().getErrors().stream()
            .limit(MAX_ERROR_NUMBER)
            .map(Notification.Error::message)
            .toList();
    }
}
