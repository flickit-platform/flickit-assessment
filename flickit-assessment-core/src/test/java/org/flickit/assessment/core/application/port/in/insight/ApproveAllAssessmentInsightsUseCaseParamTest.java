package org.flickit.assessment.core.application.port.in.insight;

import jakarta.validation.ConstraintViolationException;
import org.flickit.assessment.core.application.port.in.insight.ApproveAllAssessmentInsightsUseCase.Param;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.APPROVE_ALL_ASSESSMENT_INSIGHTS_ASSESSMENT_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ApproveAllAssessmentInsightsUseCaseParamTest {

    @Test
    void testApproveAllAssessmentInsightsUseCaseParam_assessmentIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.assessmentId(null)));
        assertThat(throwable).hasMessage("assessmentId: " + APPROVE_ALL_ASSESSMENT_INSIGHTS_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void testApproveAllAssessmentInsightsUseCaseParam_currentUserIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<Param.ParamBuilder> changer) {
        var param = paramBuilder();
        changer.accept(param);
        param.build();
    }

    private Param.ParamBuilder paramBuilder() {
        return Param.builder()
            .assessmentId(UUID.randomUUID())
            .currentUserId(UUID.randomUUID());
    }
}