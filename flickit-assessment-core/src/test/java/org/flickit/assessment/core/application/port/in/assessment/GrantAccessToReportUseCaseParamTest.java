package org.flickit.assessment.core.application.port.in.assessment;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_EMAIL_FORMAT_NOT_VALID;
import static org.flickit.assessment.core.common.ErrorMessageKey.GRANT_ACCESS_TO_REPORT_ASSESSMENT_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.GRANT_ACCESS_TO_REPORT_EMAIL_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GrantAccessToReportUseCaseParamTest {

    @Test
    void testGrantAccessToReportUseCaseParam_emailParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.email(null)));
        assertThat(throwable).hasMessage("email: " + GRANT_ACCESS_TO_REPORT_EMAIL_NOT_NULL);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.email("invalid-email-format")));
        assertThat(throwable).hasMessage("email: " + COMMON_EMAIL_FORMAT_NOT_VALID);
    }

    @Test
    void testGrantAccessToReportUseCaseParam_assessmentIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.assessmentId(null)));
        assertThat(throwable).hasMessage("assessmentId: " + GRANT_ACCESS_TO_REPORT_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void testGrantAccessToReportUseCaseParam_currentUserIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<GrantAccessToReportUseCase.Param.ParamBuilder> changer) {
        var param = paramBuilder();
        changer.accept(param);
        param.build();
    }

    private GrantAccessToReportUseCase.Param.ParamBuilder paramBuilder() {
        return GrantAccessToReportUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .email("test@test.com")
            .currentUserId(UUID.randomUUID());
    }
}
