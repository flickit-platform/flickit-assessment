package org.flickit.assessment.kit.adapter.in.rest.exception;

import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.exception.api.ErrorResponseDto;
import org.flickit.assessment.kit.adapter.out.uploaddsl.exception.DSLHasErrorException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.flickit.assessment.kit.adapter.in.rest.exception.api.ErrorCodes.DSL_HAS_ERROR;

@Slf4j
@RestControllerAdvice
public class DSLHasErrorExceptionHandler {

    @ResponseBody
    @ExceptionHandler(DSLHasErrorException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    ErrorResponseDto handle(DSLHasErrorException ex) {
        log.error("Dsl has error", ex);
        return new ErrorResponseDto(DSL_HAS_ERROR, MessageBundle.message(ex.getMessage()));
    }
}
