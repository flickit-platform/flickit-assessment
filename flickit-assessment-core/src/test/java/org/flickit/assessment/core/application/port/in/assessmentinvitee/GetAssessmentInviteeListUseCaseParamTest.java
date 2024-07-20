package org.flickit.assessment.core.application.port.in.assessmentinvitee;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GetAssessmentInviteeListUseCaseParamTest {

    @Test
    void testGetAssessmentInviteeListParam_AssessmentIdIsNull_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetAssessmentInviteeListUseCase.Param(null, currentUserId, 10, 0));
        assertThat(throwable).hasMessage("assessmentId: " + GET_ASSESSMENT_INVITEE_LIST_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void testGetAssessmentInviteeListParam_CurrentUserIdIsNull_ErrorMessage() {
        UUID assessmentId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetAssessmentInviteeListUseCase.Param(assessmentId, null, 10, 0));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    @Test
    void testGetAssessmentInviteeListParam_sizeLessThanMin_ErrorMessage() {
        UUID assessmentId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        var size = -1;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetAssessmentInviteeListUseCase.Param(assessmentId, currentUserId, size, 0));
        assertThat(throwable).hasMessage("size: " + GET_ASSESSMENT_INVITEE_LIST_SIZE_MIN);
    }

    @Test
    void testGetAssessmentInviteeListParam_sizeGreaterThanMax_ErrorMessage() {
        UUID assessmentId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        var size = 101;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetAssessmentInviteeListUseCase.Param(assessmentId, currentUserId, size, 0));
        assertThat(throwable).hasMessage("size: " + GET_ASSESSMENT_INVITEE_LIST_SIZE_MAX);
    }

    @Test
    void testGetAssessmentInviteeListParam_PageLessThanMin_ErrorMessage() {
        UUID assessmentId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        var page = -1;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetAssessmentInviteeListUseCase.Param(assessmentId, currentUserId,10, page));
        assertThat(throwable).hasMessage("page: " + GET_ASSESSMENT_INVITEE_LIST_PAGE_MIN);
    }

}
