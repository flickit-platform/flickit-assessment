package org.flickit.assessment.core.application.port.in.assessmentinsight;

import jakarta.validation.ConstraintViolationException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CreateAssessmentInsightUseCaseParamTest {

    @Test
    void testCreateAssessmentInsightUseCaseParam_assessmentIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.assessmentId(null)));
        assertThat(throwable).hasMessage("assessmentId: " + CREATE_ASSESSMENT_INSIGHT_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void testCreateAssessmentInsightUseCaseParam_insightParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.insight(null)));
        assertThat(throwable).hasMessage("insight: " + CREATE_ASSESSMENT_INSIGHT_INSIGHT_NOT_NULL);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.insight("   ab  ")));
        assertThat(throwable).hasMessage("insight: " + CREATE_ASSESSMENT_INSIGHT_INSIGHT_SIZE_MIN);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.insight(RandomStringUtils.random(1001))));
        assertThat(throwable).hasMessage("insight: " + CREATE_ASSESSMENT_INSIGHT_INSIGHT_SIZE_MAX);
    }

    @Test
    void testCreateAssessmentInsightUseCaseParam_currentUserIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    @Test
    void testCreateAssessmentInsightUseCaseParam_validParams_Successful() {
        UUID assessmentId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        String insight = RandomStringUtils.random(20);
        assertDoesNotThrow(() -> new CreateAssessmentInsightUseCase.Param(assessmentId, insight, currentUserId));
    }

    private void createParam(Consumer<CreateAssessmentInsightUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private CreateAssessmentInsightUseCase.Param.ParamBuilder paramBuilder() {
        return CreateAssessmentInsightUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .insight("insight")
            .currentUserId(UUID.randomUUID());
    }
}
