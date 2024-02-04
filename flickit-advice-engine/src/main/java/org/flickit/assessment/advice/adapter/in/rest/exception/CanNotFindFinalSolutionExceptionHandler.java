package org.flickit.assessment.advice.adapter.in.rest.exception;

import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.advice.application.exception.FinalSolutionNotFoundException;
import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.exception.api.ErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.flickit.assessment.advice.adapter.in.rest.exception.api.ErrorCodes.COMPUTING_ADVICE_ERROR;

@Slf4j
@RestControllerAdvice
public class CanNotFindFinalSolutionExceptionHandler {

    @ResponseBody
    @ExceptionHandler(FinalSolutionNotFoundException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    ErrorResponseDto handle(FinalSolutionNotFoundException ex) {
        log.error("Calculate advice error", ex);
        return new ErrorResponseDto(COMPUTING_ADVICE_ERROR, MessageBundle.message(ex.getMessage()));
    }
}
