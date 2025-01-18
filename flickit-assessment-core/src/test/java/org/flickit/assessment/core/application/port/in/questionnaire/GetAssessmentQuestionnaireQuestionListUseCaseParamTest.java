package org.flickit.assessment.core.application.port.in.questionnaire;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GetAssessmentQuestionnaireQuestionListUseCaseParamTest {

    @Test
    void testGetAssessmentQuestionnaireQuestionListUseCaseParam_AssessmentIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.assessmentId(null)));
        assertThat(throwable).hasMessage("assessmentId: " + GET_ASSESSMENT_QUESTIONNAIRE_QUESTION_LIST_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void testGetAssessmentQuestionnaireQuestionListUseCaseParam_QuestionnaireIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.questionnaireId(null)));
        assertThat(throwable).hasMessage("questionnaireId: " + GET_ASSESSMENT_QUESTIONNAIRE_QUESTION_LIST_QUESTIONNAIRE_ID_NOT_NULL);
    }

    @Test
    void testGetAssessmentQuestionnaireQuestionListUseCaseParam_SizeParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.size(0)));
        assertThat(throwable).hasMessage("size: " + GET_ASSESSMENT_QUESTIONNAIRE_QUESTION_LIST_SIZE_MIN);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.size(101)));
        assertThat(throwable).hasMessage("size: " + GET_ASSESSMENT_QUESTIONNAIRE_QUESTION_LIST_SIZE_MAX);
    }

    @Test
    void testGetAssessmentQuestionnaireQuestionListUseCaseParam_PageNumberParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.page(-1)));
        assertThat(throwable).hasMessage("page: " + GET_ASSESSMENT_QUESTIONNAIRE_QUESTION_LIST_PAGE_MIN);
    }

    @Test
    void testGetAssessmentQuestionnaireQuestionListUseCaseParam_CurrentUserIdViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<GetAssessmentQuestionnaireQuestionListUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private GetAssessmentQuestionnaireQuestionListUseCase.Param.ParamBuilder paramBuilder() {
        return GetAssessmentQuestionnaireQuestionListUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .questionnaireId(0L)
            .page(0)
            .size(10)
            .currentUserId(UUID.randomUUID());
    }
}
