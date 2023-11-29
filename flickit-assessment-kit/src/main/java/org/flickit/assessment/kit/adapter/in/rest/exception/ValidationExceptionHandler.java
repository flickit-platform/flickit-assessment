package org.flickit.assessment.kit.adapter.in.rest.exception;

import org.flickit.assessment.kit.adapter.in.rest.exception.api.ErrorResponsesDto;
import org.flickit.assessment.kit.common.Notification;
import org.flickit.assessment.kit.common.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

import static org.flickit.assessment.kit.adapter.in.rest.exception.api.ErrorCodes.UNSUPPORTED_DSL_CONTENT_CHANGE;

@RestControllerAdvice
public class ValidationExceptionHandler {

    private static final short MAX_ERROR_NUMBER = 10;

    @ResponseBody
    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponsesDto handle(ValidationException ex) {
        return new ErrorResponsesDto(UNSUPPORTED_DSL_CONTENT_CHANGE, collectMsgList(ex));
    }

    private List<String> collectMsgList(ValidationException ex) {
        return ex.getValidation().getErrors().stream()
            .limit(MAX_ERROR_NUMBER)
            .map(Notification.Error::message)
            .toList();
    }
}
