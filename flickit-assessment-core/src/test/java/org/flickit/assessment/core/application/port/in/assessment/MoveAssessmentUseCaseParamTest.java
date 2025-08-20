package org.flickit.assessment.core.application.port.in.assessment;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.*;

class MoveAssessmentUseCaseParamTest {

    @Test
    void testMoveAssessmentSpaceUseCaseParam_assessmentIdParamIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.assessmentId(null)));
        assertThat(throwable).hasMessage("assessmentId: " + MOVE_ASSESSMENT_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void testMoveAssessmentSpaceUseCaseParam_targetSpaceIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.targetSpaceId(null)));
        assertThat(throwable).hasMessage("targetSpaceId: " + MOVE_ASSESSMENT_TARGET_SPACE_ID_NOT_NULL);
    }

    @Test
    void testMoveAssessmentUseCaseParam_currentUserIdParamIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<MoveAssessmentUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private MoveAssessmentUseCase.Param.ParamBuilder paramBuilder() {
        return MoveAssessmentUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .targetSpaceId(1L)
            .currentUserId(UUID.randomUUID());
    }
}
