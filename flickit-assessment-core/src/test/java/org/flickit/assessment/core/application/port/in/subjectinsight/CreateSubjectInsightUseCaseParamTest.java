package org.flickit.assessment.core.application.port.in.subjectinsight;

import jakarta.validation.ConstraintViolationException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CreateSubjectInsightUseCaseParamTest {

    @Test
    void testCreateSubjectInsightUseCaseParam_assessmentIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.assessmentId(null)));
        assertThat(throwable).hasMessage("assessmentId: " + CREATE_SUBJECT_INSIGHT_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void testCreateSubjectInsightUseCaseParam_subjectIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.subjectId(null)));
        assertThat(throwable).hasMessage("subjectId: " + CREATE_SUBJECT_INSIGHT_SUBJECT_ID_NOT_NULL);
    }

    @Test
    void testCreateSubjectInsightUseCaseParam_insightParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.insight(null)));
        assertThat(throwable).hasMessage("insight: " + CREATE_SUBJECT_INSIGHT_INSIGHT_NOT_NULL);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.insight(" ab ")));
        assertThat(throwable).hasMessage("insight: " + CREATE_SUBJECT_INSIGHT_INSIGHT_SIZE_MIN);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.insight(RandomStringUtils.random(1001))));
        assertThat(throwable).hasMessage("insight: " + CREATE_SUBJECT_INSIGHT_INSIGHT_SIZE_MAX);
    }

    @Test
    void testCreateSubjectInsightUseCaseParam_currentUserIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<CreateSubjectInsightUseCase.Param.ParamBuilder> changer) {
        var param = paramBuilder();
        changer.accept(param);
        param.build();
    }

    private CreateSubjectInsightUseCase.Param.ParamBuilder paramBuilder() {
        return CreateSubjectInsightUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .subjectId(1L)
            .insight("subject insight")
            .currentUserId(UUID.randomUUID());
    }
}
