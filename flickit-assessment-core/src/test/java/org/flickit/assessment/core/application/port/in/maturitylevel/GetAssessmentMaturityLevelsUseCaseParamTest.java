package org.flickit.assessment.core.application.port.in.maturitylevel;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_ASSESSMENT_MATURITY_LEVELS_ASSESSMENT_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;

class GetAssessmentMaturityLevelsUseCaseParamTest {

    @Test
    void testGetAssessmentMaturityLevelsUseCaseParam_assessmentIdParamViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.assessmentId(null)));
        assertThat(throwable).hasMessage("assessmentId: " + GET_ASSESSMENT_MATURITY_LEVELS_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void testGetAssessmentMaturityLevelsUseCaseParam_currentUserIdParamViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<GetAssessmentMaturityLevelsUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private GetAssessmentMaturityLevelsUseCase.Param.ParamBuilder paramBuilder() {
        return GetAssessmentMaturityLevelsUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .currentUserId(UUID.randomUUID());
    }
}
