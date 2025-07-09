package org.flickit.assessment.core.application.port.in.questionnaire;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GetAssessmentNextQuestionnaireUseCaseParamTest {

    @Test
    void testGetAssessmentNextQuestionnaireUseCaseParam_AssessmentIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.assessmentId(null)));
        assertThat(throwable).hasMessage("assessmentId: " + GET_ASSESSMENT_NEXT_QUESTIONNAIRE_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void testGetAssessmentNextQuestionnaireUseCaseParam_QuestionnaireIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.questionnaireId(null)));
        assertThat(throwable).hasMessage("questionnaireId: " + GET_ASSESSMENT_NEXT_QUESTIONNAIRE_QUESTIONNAIRE_ID_NOT_NULL);
    }

    @Test
    void testGetAssessmentNextQuestionnaireUseCaseParam_CurrentUserIdViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<GetAssessmentNextQuestionnaireUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private GetAssessmentNextQuestionnaireUseCase.Param.ParamBuilder paramBuilder() {
        return GetAssessmentNextQuestionnaireUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .questionnaireId(1L)
            .currentUserId(UUID.randomUUID());
    }
}
