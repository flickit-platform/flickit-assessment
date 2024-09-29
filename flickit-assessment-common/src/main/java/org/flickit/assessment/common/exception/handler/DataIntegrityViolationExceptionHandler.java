package org.flickit.assessment.common.exception.handler;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.exception.api.ErrorCodes;
import org.flickit.assessment.common.exception.api.ErrorResponseDto;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.NonTransientDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.flickit.assessment.common.adapter.out.persistence.util.ConstraintExtractor.extractConstraint;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DataIntegrityViolationExceptionHandler {

    private final List<DataConstraintErrorMapper> collectors;

    @ResponseBody
    @ExceptionHandler({DataIntegrityViolationException.class, JpaSystemException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ErrorResponseDto handle(NonTransientDataAccessException ex) {
        final String constraintName = extractConstraintIfPossible(ex);
        return constraintToErrorMsg(constraintName)
            .map(errorMsg -> {
                String message = MessageBundle.message(errorMsg);
                return new ErrorResponseDto(ErrorCodes.INVALID_INPUT, message);
            }).orElseGet(() -> {
                log.error("Not translated db constraint violation error occurred.", ex);
                return new ErrorResponseDto(ErrorCodes.INVALID_INPUT, "UNKNOWN");
            });
    }

    private Optional<String> constraintToErrorMsg(@Nullable String constraintName) {
        if (constraintName == null)
            return Optional.empty();
        return collectors.stream()
            .filter(x -> x.contains(constraintName))
            .map(x -> x.errorMessage(constraintName))
            .findAny();
    }

    @Nullable
    private String extractConstraintIfPossible(NonTransientDataAccessException ex) {
        if (ex instanceof DataIntegrityViolationException e && e.getCause() instanceof ConstraintViolationException cause)
            return extractConstraint(cause.getConstraintName());
        if (ex instanceof JpaSystemException && ex.getCause() != null && ex.getCause().getCause() != null) {
            String msg = ex.getCause().getCause().getMessage(); // org.postgresql.util.PSQLException
            Pattern pattern = Pattern.compile("foreign key constraint \"(.+)\"");
            Matcher matcher = pattern.matcher(msg);
            if (matcher.find())
               return matcher.group(1);
            return null;
        } else if (ex instanceof DuplicateKeyException && ex.getCause() != null) {
            String msg = ex.getCause().getMessage(); // org.postgresql.util.PSQLException
            Pattern pattern = Pattern.compile("unique constraint \"(.+)\"");
            Matcher matcher = pattern.matcher(msg);
            if (matcher.find())
                return matcher.group(1);
            return null;
        }
        return null;
    }
}
