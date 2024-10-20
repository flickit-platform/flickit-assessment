package org.flickit.assessment.kit.adapter.in.rest.exception;

import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.common.exception.api.ErrorResponseDto;
import org.flickit.assessment.kit.adapter.out.rest.exception.DSLSyntaxErrorException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.flickit.assessment.kit.adapter.in.rest.exception.api.ErrorCodes.DSL_SYNTAX_ERROR;

@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DSLSyntaxErrorExceptionHandler {

    @ResponseBody
    @ExceptionHandler(DSLSyntaxErrorException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    ErrorResponseDto handle(DSLSyntaxErrorException ex) {
        return new ErrorResponseDto(DSL_SYNTAX_ERROR, ex.getSyntaxError());
    }
}
