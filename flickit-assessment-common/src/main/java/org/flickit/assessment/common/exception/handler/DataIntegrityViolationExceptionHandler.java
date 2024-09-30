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

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DataIntegrityViolationExceptionHandler {

    private final List<DataConstraintErrorMapper> collectors;

    /**
     * - oracle, postgres, mysql pattern: 'schemaName:constraintName' <p>
     * - h2 pattern: schemaName:constraintName_index_d .*
     */
    private static final Pattern CONSTRAINT_NAME_PATTERN = Pattern.compile("\\.([a-z]+)");
    private static final Pattern POSTGRES_FK_CONSTRAINT_PATTERN = Pattern.compile("foreign key constraint \"(.+)\"");
    private static final Pattern POSTGRES_UQ_CONSTRAINT_PATTERN = Pattern.compile("unique constraint \"(.+)\"");

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
            return extractConstraint(POSTGRES_FK_CONSTRAINT_PATTERN, msg);
        } else if (ex instanceof DuplicateKeyException && ex.getCause() != null) {
            String msg = ex.getCause().getMessage(); // org.postgresql.util.PSQLException
            return extractConstraint(POSTGRES_UQ_CONSTRAINT_PATTERN, msg);
        }
        if (ex.getCause() instanceof SQLException e) {
            String msg = e.getMessage(); // org.postgresql.util.PSQLException
            Pattern pattern = Pattern.compile("foreign key constraint \"(.+)\"");
            Matcher matcher = pattern.matcher(msg);
            if (matcher.find())
                return matcher.group(1);
            return null;
        }
        return null;
    }

    @Nullable
    public static String extractConstraint(@Nullable String rawConstraint) {
        String finalConstraint = doExtractConstraint(rawConstraint);
        log.debug("Extract constraint: [{}] => [{}]", rawConstraint, finalConstraint);
        return finalConstraint;
    }

    @Nullable
    private static String doExtractConstraint(@Nullable String rawConstraint) {
        if (isBlank(rawConstraint))
            return null;
        String normalizedConstraint = normalize(rawConstraint);
        Matcher matcher = CONSTRAINT_NAME_PATTERN.matcher(normalizedConstraint);
        if (matcher.find())
            return matcher.group(1);
        return normalizedConstraint;
    }

    @Nullable
    private static String extractConstraint(Pattern pattern, String msg) {
        Matcher matcher = pattern.matcher(msg);
        if (matcher.find()) {
            String constraint = normalize(matcher.group(1));
            log.debug("Extract constraint: [{}] => [{}]", msg, constraint);
            return constraint;
        }
        log.debug("Extract constraint: [{}] => [null]", msg);
        return null;
    }

    private static String normalize(String constraint) {
        return constraint.toLowerCase();
    }
}
