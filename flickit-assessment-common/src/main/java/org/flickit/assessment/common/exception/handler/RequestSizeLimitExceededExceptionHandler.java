package org.flickit.assessment.common.exception.handler;

import io.sentry.Sentry;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.exception.api.ErrorResponseDto;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.flickit.assessment.common.error.ErrorMessageKey.UPLOAD_FILE_SIZE_MAX;
import static org.flickit.assessment.common.exception.api.ErrorCodes.INVALID_INPUT;

@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestSizeLimitExceededExceptionHandler {

    @ResponseBody
    @ExceptionHandler(SizeLimitExceededException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ErrorResponseDto handle(SizeLimitExceededException ex) {
        Sentry.captureException(ex);
        log.error(ex.getMessage());
        return new ErrorResponseDto(INVALID_INPUT, MessageBundle.message(UPLOAD_FILE_SIZE_MAX));
    }
}
