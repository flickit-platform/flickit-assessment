package org.flickit.assessment.core.application.port.in.assessmentinvitee;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.DELETE_ASSESSMENT_INVITEE_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class DeleteAssessmentInviteeUseCaseParamTest {

    @Test
    void testDeleteAssessmentInviteeParam_IdIsNull_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new DeleteAssessmentInviteeUseCase.Param(null, currentUserId));
        assertThat(throwable).hasMessage("id: " + DELETE_ASSESSMENT_INVITEE_ID_NOT_NULL);
    }

    @Test
    void testDeleteAssessmentInviteeParam_CurrentUserIdIsNull_ErrorMessage() {
        UUID id = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new DeleteAssessmentInviteeUseCase.Param(id, null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }
}
