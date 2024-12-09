package org.flickit.assessment.core.application.port.in.attribute;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GetAttributeScoreStatsUseCaseParamTest {

    @Test
    void testGetAttributeScoreStatsUseCaseParam_AssessmentIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.assessmentId(null)));
        assertThat(throwable).hasMessage("assessmentId: " + GET_ATTRIBUTE_SCORE_STATS_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void testGetAttributeScoreStatsUseCaseParam_AttributeIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.attributeId(null)));
        assertThat(throwable).hasMessage("attributeId: " + GET_ATTRIBUTE_SCORE_STATS_ATTRIBUTE_ID_NOT_NULL);
    }

    @Test
    void testGetAttributeScoreStatsUseCaseParam_MaturityLevelIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.maturityLevelId(null)));
        assertThat(throwable).hasMessage("maturityLevelId: " + GET_ATTRIBUTE_SCORE_STATS_MATURITY_LEVEL_ID_NOT_NULL);
    }

    @Test
    void testGetAttributeScoreStatsUseCaseParam_CurrentUserIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<GetAttributeScoreStatsUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private GetAttributeScoreStatsUseCase.Param.ParamBuilder paramBuilder() {
        return GetAttributeScoreStatsUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .attributeId(1L)
            .maturityLevelId(1L)
            .currentUserId(UUID.randomUUID());
    }
}
