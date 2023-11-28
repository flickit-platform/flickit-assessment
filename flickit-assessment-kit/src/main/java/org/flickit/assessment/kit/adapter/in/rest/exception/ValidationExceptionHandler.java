package org.flickit.assessment.kit.adapter.in.rest.exception;

import org.flickit.assessment.kit.adapter.in.rest.exception.api.ErrorResponseDto;
import org.flickit.assessment.kit.common.MessageBundle;
import org.flickit.assessment.kit.common.Notification;
import org.flickit.assessment.kit.common.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.flickit.assessment.kit.adapter.in.rest.exception.api.ErrorCodes.NOT_ALLOWED_DSL_CONTENT_CHANGE;

@RestControllerAdvice
public class ValidationExceptionHandler {

    private static final short MAX_ERROR_NUMBERS = 10;
    private static final String MESSAGE_DELIMITER = "\n";

    @ResponseBody
    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handle(ValidationException ex) {
        String messages = toMessage(ex);
        return new ErrorResponseDto(NOT_ALLOWED_DSL_CONTENT_CHANGE, messages);
    }

    private String toMessage(ValidationException ex) {
        StringBuilder result = new StringBuilder();
        var errors = ex.getValidation().getErrors().stream()
            .limit(MAX_ERROR_NUMBERS)
            .toList();
        for (Notification.Error error : errors) {
            result.append(String.format(MessageBundle.message(error.errorMessage()), error.values())).append(MESSAGE_DELIMITER);
        }
        return result.toString();
    }
}
