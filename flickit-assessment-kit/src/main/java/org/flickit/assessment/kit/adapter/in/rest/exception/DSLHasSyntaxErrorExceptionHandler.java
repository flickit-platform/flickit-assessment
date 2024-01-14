package org.flickit.assessment.kit.adapter.in.rest.exception;

import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.common.exception.api.ErrorResponseDto;
import org.flickit.assessment.kit.adapter.out.uploaddsl.exception.DSLHasSyntaxErrorException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.flickit.assessment.kit.adapter.in.rest.exception.api.ErrorCodes.DSL_HAS_SYNTAX_ERROR;

@Slf4j
@RestControllerAdvice
public class DSLHasSyntaxErrorExceptionHandler {

    @ResponseBody
    @ExceptionHandler(DSLHasSyntaxErrorException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ErrorResponseDto handle(DSLHasSyntaxErrorException ex) {
        log.error("DSL content has syntax error ", ex);
        return new ErrorResponseDto(DSL_HAS_SYNTAX_ERROR, ex.getMessage());
    }
}
