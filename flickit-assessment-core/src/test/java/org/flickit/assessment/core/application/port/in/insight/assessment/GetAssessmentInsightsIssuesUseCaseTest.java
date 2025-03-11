package org.flickit.assessment.core.application.port.in.insight.assessment;

import jakarta.validation.ConstraintViolationException;
import org.flickit.assessment.core.application.port.in.insight.GetAssessmentInsightsIssuesUsecase;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_ASSESSMENT_INSIGHTS_ISSUES_ASSESSMENT_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.*;

class GetAssessmentInsightsIssuesUseCaseParamTest {

    @Test
    void testGetAssessmentInsightsIssuesUseCaseParam_assessmentIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.assessmentId(null)));
        assertThat(throwable).hasMessage("assessmentId: " + GET_ASSESSMENT_INSIGHTS_ISSUES_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void testGetAssessmentInsightsIssuesUseCaseParam_currentUserIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<GetAssessmentInsightsIssuesUseCase.Param.ParamBuilder> changer) {
        var param = paramBuilder();
        changer.accept(param);
        param.build();
    }

    private GetAssessmentInsightsIssuesUseCase.Param.ParamBuilder paramBuilder() {
        return GetAssessmentInsightsIssuesUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .currentUserId(UUID.randomUUID());
    }
}
