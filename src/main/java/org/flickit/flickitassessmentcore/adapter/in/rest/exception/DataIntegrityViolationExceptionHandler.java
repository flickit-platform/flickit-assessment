package org.flickit.flickitassessmentcore.adapter.in.rest.exception;

import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.flickit.flickitassessmentcore.adapter.in.rest.exception.api.ErrorCodes;
import org.flickit.flickitassessmentcore.adapter.in.rest.exception.api.ErrorResponseDto;
import org.flickit.flickitassessmentcore.common.MessageBundle;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

import static org.flickit.flickitassessmentcore.adapter.out.persistence.util.ConstraintExtractor.extractConstraint;
import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.*;

@Slf4j
@RestControllerAdvice
public class DataIntegrityViolationExceptionHandler {

    private final Map<String, String> constraintToErrorMsg = new HashMap<>();

    {
        constraintToErrorMsg.put("uq_fac_assessment_spaceid_code", CREATE_ASSESSMENT_DUPLICATE_TITLE_AND_SPACE_ID);
        constraintToErrorMsg.put("fk_fac_assessmentresult_assessment", CREATE_ASSESSMENT_RESULT_ASSESSMENT_ID_NOT_FOUND);
        constraintToErrorMsg.put("fk_fac_qualityattributevalue_assessmentresult", CREATE_QUALITY_ATTRIBUTE_VALUE_ASSESSMENT_RESULT_ID_NOT_FOUND);
        constraintToErrorMsg.put("fk_fac_subjectvalue_assessmentresult", CREATE_SUBJECT_VALUE_ASSESSMENT_RESULT_ID_NOT_FOUND);
        constraintToErrorMsg.put("fk_fac_answer_assessmentresult", SUBMIT_ANSWER_ASSESSMENT_RESULT_ID_NOT_FOUND);
    }

    @ResponseBody
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ErrorResponseDto handle(DataIntegrityViolationException ex) {
        final String constraintName = extractConstraintIfPossible(ex);
        if (constraintName != null && constraintToErrorMsg.containsKey(constraintName)) {
            String message = MessageBundle.message(constraintToErrorMsg.get(constraintName));
            return new ErrorResponseDto(ErrorCodes.INVALID_INPUT, message);
        }
        log.error("Not translated db constraint violation error occurred.", ex);
        return new ErrorResponseDto(ErrorCodes.INVALID_INPUT, "UNKNOWN");
    }

    @Nullable
    private String extractConstraintIfPossible(DataIntegrityViolationException ex) {
        if (ex.getCause() instanceof ConstraintViolationException)
            return extractConstraint(((ConstraintViolationException) ex.getCause()).getConstraintName());
        return null;
    }
}
