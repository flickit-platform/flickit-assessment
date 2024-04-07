package org.flickit.assessment.core.application.port.in.questionnaire;

import jakarta.validation.ConstraintViolationException;
import org.flickit.assessment.core.application.port.in.questionnaire.GetAssessmentQuestionnaireListUseCase.Param;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class GetAssessmentQuestionnaireListUseCaseParamTest {

    private static final UUID ASSESSMENT_ID = UUID.randomUUID();
    private static final UUID CURRENT_USER_ID = UUID.randomUUID();
    private static final int SIZE = 10;
    private static final int PAGE = 0;

    @Test
    void testGetQuestionnaireListParam_assessmentIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new Param(null, CURRENT_USER_ID, SIZE, PAGE));
        assertThat(throwable).hasMessage("assessmentId: " + GET_ASSESSMENT_QUESTIONNAIRE_LIST_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void testGetQuestionnaireListParam_currentUserIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new Param(ASSESSMENT_ID, null, SIZE, PAGE));
        assertThat(throwable).hasMessage("currentUserId: " + GET_ASSESSMENT_QUESTIONNAIRE_LIST_CURRENT_USER_ID_NOT_NULL);
    }

    @Test
    void testGetQuestionnaireListParam_sizeLessThanMin_ErrorMessage() {
        var size = -1;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new Param(ASSESSMENT_ID, CURRENT_USER_ID, size, PAGE));
        assertThat(throwable).hasMessage("size: " + GET_ASSESSMENT_QUESTIONNAIRE_LIST_SIZE_MIN);
    }

    @Test
    void testGetQuestionnaireListParam_sizeGreaterThanMax_ErrorMessage() {
        var size = 51;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new Param(ASSESSMENT_ID, CURRENT_USER_ID, size, PAGE));
        assertThat(throwable).hasMessage("size: " + GET_ASSESSMENT_QUESTIONNAIRE_LIST_SIZE_MAX);
    }

    @Test
    void testGetQuestionnaireListParam_PageLessThanMin_ErrorMessage() {
        var page = -1;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new Param(ASSESSMENT_ID, CURRENT_USER_ID, SIZE, page));
        assertThat(throwable).hasMessage("page: " + GET_ASSESSMENT_QUESTIONNAIRE_LIST_PAGE_MIN);
    }
}
