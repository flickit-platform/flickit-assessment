package org.flickit.assessment.kit.application.port.in.questionnaire;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GetQuestionnaireListUseCaseParamTest {

    @Test
    void testGetQuestionnaireListParam_assessmentIdIsNull_ErrorMessage() {
        var currentUserId = UUID.randomUUID();
        var size = 10;
        var page = 0;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetQuestionnaireListUseCase.Param(null, currentUserId, size, page));
        assertThat(throwable).hasMessage("assessmentId: " + GET_QUESTIONNAIRE_LIST_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void testGetQuestionnaireListParam_currentUserIdIsNull_ErrorMessage() {
        var assessmentId = UUID.randomUUID();
        var size = 10;
        var page = 0;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetQuestionnaireListUseCase.Param(assessmentId, null, size, page));
        assertThat(throwable).hasMessage("currentUserId: " + GET_QUESTIONNAIRE_LIST_CURRENT_USER_ID_NOT_NULL);
    }

    @Test
    void testGetQuestionnaireListParam_sizeLessThanMin_ErrorMessage() {
        var assessmentId = UUID.randomUUID();
        var currentUserId = UUID.randomUUID();
        var size = -1;
        var page = 0;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetQuestionnaireListUseCase.Param(assessmentId, currentUserId, size, page));
        assertThat(throwable).hasMessage("size: " + GET_QUESTIONNAIRE_LIST_SIZE_MIN);
    }

    @Test
    void testGetQuestionnaireListParam_sizeGreaterThanMax_ErrorMessage() {
        var assessmentId = UUID.randomUUID();
        var currentUserId = UUID.randomUUID();
        var size = 51;
        var page = 0;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetQuestionnaireListUseCase.Param(assessmentId, currentUserId, size, page));
        assertThat(throwable).hasMessage("size: " + GET_QUESTIONNAIRE_LIST_SIZE_MAX);
    }

    @Test
    void testGetQuestionnaireListParam_PageLessThanMin_ErrorMessage() {
        var assessmentId = UUID.randomUUID();
        var currentUserId = UUID.randomUUID();
        var size = 10;
        var page = -1;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetQuestionnaireListUseCase.Param(assessmentId, currentUserId, size, page));
        assertThat(throwable).hasMessage("page: " + GET_QUESTIONNAIRE_LIST_PAGE_MIN);
    }
}
