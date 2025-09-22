package org.flickit.assessment.core.application.port.in.assessmentreport;

import jakarta.validation.ConstraintViolationException;
import org.flickit.assessment.core.application.domain.VisibilityType;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.UPDATE_ASSESSMENT_REPORT_VISIBILITY_VISIBILITY_INVALID;
import static org.flickit.assessment.core.common.ErrorMessageKey.UPDATE_ASSESSMENT_REPORT_VISIBILITY_VISIBILITY_NOT_NULL;
import static org.junit.jupiter.api.Assertions.*;

class UpdateAssessmentReportVisibilityUseCaseParamTest {

    @Test
    void testUpdateAssessmentReportVisibilityParam_whenVisibilityHasValidValue_success() {
        assertDoesNotThrow(() -> createParam(b -> b.visibility("PUBLIC")));
    }

    @Test
    void testUpdateAssessmentReportVisibilityParam_visibilityParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.visibility(null)));
        assertThat(throwable).hasMessage("visibility: " + UPDATE_ASSESSMENT_REPORT_VISIBILITY_VISIBILITY_NOT_NULL);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.visibility("Something")));
        assertThat(throwable).hasMessage("visibility: " + UPDATE_ASSESSMENT_REPORT_VISIBILITY_VISIBILITY_INVALID);
    }

    @Test
    void testUpdateAssessmentReportVisibilityParam_currentUserIdParamViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<UpdateAssessmentReportVisibilityUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private UpdateAssessmentReportVisibilityUseCase.Param.ParamBuilder paramBuilder() {
        return UpdateAssessmentReportVisibilityUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .visibility(VisibilityType.RESTRICTED.name())
            .currentUserId(UUID.randomUUID());
    }
}
