package org.flickit.assessment.common.exception.handler;

import org.flickit.assessment.common.exception.ValidationsException;
import org.flickit.assessment.common.exception.api.ErrorResponsesDto;
import org.flickit.assessment.common.exception.api.Notification;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ValidationsExceptionHandler {

    private static final short MAX_ERROR_NUMBER = 10;

    @ResponseBody
    @ExceptionHandler(ValidationsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponsesDto handle(ValidationsException ex) {
        return new ErrorResponsesDto(ex.getCode(), collectMsgList(ex));
    }

    private List<String> collectMsgList(ValidationsException ex) {
        return ex.getValidation().getErrors().stream()
            .limit(MAX_ERROR_NUMBER)
            .map(Notification.Error::message)
            .toList();
    }
}
