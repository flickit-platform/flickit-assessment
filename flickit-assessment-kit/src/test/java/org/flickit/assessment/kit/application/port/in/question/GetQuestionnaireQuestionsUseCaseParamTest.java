package org.flickit.assessment.kit.application.port.in.question;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.*;

class GetQuestionnaireQuestionsUseCaseParamTest {

    @Test
    void testGetQuestionnaireQuestionsUseCaseParam_kitVersionIdParamViolatesConstraints_ErrorMessage() {
        ConstraintViolationException throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.kitVersionId(null)));
        assertThat(throwable).hasMessage("kitVersionId: " + GET_QUESTIONNAIRE_QUESTIONS_KIT_VERSION_ID_NOT_NULL);
    }

    @Test
    void testGetQuestionnaireQuestionsUseCaseParam_questionnaireIdParamViolatesConstraints_ErrorMessage() {
        ConstraintViolationException throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.questionnaireId(null)));
        assertThat(throwable).hasMessage("questionnaireId: " + GET_QUESTIONNAIRE_QUESTIONS_QUESTIONNAIRE_ID_NOT_NULL);
    }

    @Test
    void testQuestionnaireQuestionsUseCaseParam_currentUserIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    @Test
    void testQuestionnaireQuestionsUseCaseParam_pageParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.page(-1)));
        assertThat(throwable).hasMessage("page: " + GET_QUESTIONNAIRE_QUESTIONS_PAGE_MIN);
    }

    @Test
    void testQuestionnaireQuestionsUseCaseParam_sizeParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.size(0)));
        assertThat(throwable).hasMessage("size: " + GET_QUESTIONNAIRE_QUESTIONS_SIZE_MIN);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.size(101)));
        assertThat(throwable).hasMessage("size: " + GET_QUESTIONNAIRE_QUESTIONS_SIZE_MAX);
    }

    public void createParam(Consumer<GetQuestionnaireQuestionsUseCase.Param.ParamBuilder> changer) {
        var param = paramBuilder();
        changer.accept(param);
        param.build();
    }

    public GetQuestionnaireQuestionsUseCase.Param.ParamBuilder paramBuilder() {
        return GetQuestionnaireQuestionsUseCase.Param.builder()
            .kitVersionId(1L)
            .questionnaireId(2L)
            .currentUserId(UUID.randomUUID())
            .page(0)
            .size(20);
    }

}
