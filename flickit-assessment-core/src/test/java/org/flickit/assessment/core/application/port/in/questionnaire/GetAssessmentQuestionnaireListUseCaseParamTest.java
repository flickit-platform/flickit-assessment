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

    @Test
    void testGetQuestionnaireListParam_assessmentIdIsNull_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new Param(null, 10, 0, currentUserId));
        assertThat(throwable).hasMessage("assessmentId: " + GET_ASSESSMENT_QUESTIONNAIRE_LIST_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void testGetQuestionnaireListParam_currentUserIdIsNull_ErrorMessage() {
        UUID assessmentId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new Param(assessmentId, 10, 0, null));
        assertThat(throwable).hasMessage("currentUserId: " + GET_ASSESSMENT_QUESTIONNAIRE_LIST_CURRENT_USER_ID_NOT_NULL);
    }

    @Test
    void testGetQuestionnaireListParam_sizeLessThanMin_ErrorMessage() {
        UUID assessmentId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        var size = -1;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new Param(assessmentId, size, 0, currentUserId));
        assertThat(throwable).hasMessage("size: " + GET_ASSESSMENT_QUESTIONNAIRE_LIST_SIZE_MIN);
    }

    @Test
    void testGetQuestionnaireListParam_sizeGreaterThanMax_ErrorMessage() {
        UUID assessmentId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        var size = 51;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new Param(assessmentId, size, 0, currentUserId));
        assertThat(throwable).hasMessage("size: " + GET_ASSESSMENT_QUESTIONNAIRE_LIST_SIZE_MAX);
    }

    @Test
    void testGetQuestionnaireListParam_PageLessThanMin_ErrorMessage() {
        UUID assessmentId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        var page = -1;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new Param(assessmentId, 10, page, currentUserId));
        assertThat(throwable).hasMessage("page: " + GET_ASSESSMENT_QUESTIONNAIRE_LIST_PAGE_MIN);
    }
}
