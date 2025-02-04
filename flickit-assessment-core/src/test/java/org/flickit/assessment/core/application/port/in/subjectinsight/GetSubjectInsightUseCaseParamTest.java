package org.flickit.assessment.core.application.port.in.subjectinsight;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_SUBJECT_INSIGHT_ASSESSMENT_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_SUBJECT_INSIGHT_SUBJECT_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GetSubjectInsightUseCaseParamTest {

    @Test
    void testGetSubjectInsightParam_assessmentIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.assessmentId(null)));

        assertThat(throwable).hasMessage("assessmentId: " + GET_SUBJECT_INSIGHT_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void testGetSubjectInsightParam_subjectIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.subjectId(null)));

        assertThat(throwable).hasMessage("subjectId: " + GET_SUBJECT_INSIGHT_SUBJECT_ID_NOT_NULL);
    }

    @Test
    void testGetSubjectInsightParam_currentUserIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));

        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<GetSubjectInsightUseCase.Param.ParamBuilder> changer) {
        var param = paramBuilder();
        changer.accept(param);
        param.build();
    }

    private GetSubjectInsightUseCase.Param.ParamBuilder paramBuilder() {
        return GetSubjectInsightUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .subjectId(1L)
            .currentUserId(UUID.randomUUID());
    }
}
