package org.flickit.assessment.advice.adapter.in.rest.exception;

import org.flickit.assessment.advice.application.exception.FinalSolutionNotFoundException;
import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.exception.api.ErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.flickit.assessment.advice.adapter.in.rest.exception.api.ErrorCodes.ADVICE_CALCULATION_ERROR;

@RestControllerAdvice
public class FinalSolutionNotFoundExceptionHandler {

    @ResponseBody
    @ExceptionHandler(FinalSolutionNotFoundException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    ErrorResponseDto handle(FinalSolutionNotFoundException ex) {
        return new ErrorResponseDto(ADVICE_CALCULATION_ERROR, MessageBundle.message(ex.getMessage()));
    }
}
