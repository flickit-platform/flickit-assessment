package org.flickit.assessment.core.application.port.in.subjectinsight;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.core.common.ErrorMessageKey.INIT_SUBJECT_INSIGHT_ASSESSMENT_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.INIT_SUBJECT_INSIGHT_SUBJECT_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.*;

class InitSubjectInsightUseCaseParamTest {

    @Test
    void testInitSubjectInsightUseCaseParam_assessmentIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.assessmentId(null)));
        assertThat(throwable).hasMessage("assessmentId: " + INIT_SUBJECT_INSIGHT_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void testInitSubjectInsightUseCaseParam_subjectIdParamIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.subjectId(null)));
        assertThat(throwable).hasMessage("subjectId: " + INIT_SUBJECT_INSIGHT_SUBJECT_ID_NOT_NULL);
    }

    private void createParam(Consumer<InitSubjectInsightUseCase.Param.ParamBuilder> changer) {
        var param = paramBuilder();
        changer.accept(param);
        param.build();
    }

    private InitSubjectInsightUseCase.Param.ParamBuilder paramBuilder() {
        return InitSubjectInsightUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .subjectId(1L);
    }
}
