package org.flickit.assessment.core.application.port.in.assessmentinvitee;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;

public interface InviteAssessmentUserUseCase {

    void inviteUser(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = INVITE_ASSESSMENT_USER_ASSESSMENT_ID_NOT_NULL)
        UUID assessmentId;

        @NotNull(message = INVITE_ASSESSMENT_USER_EMAIL_NOT_NULL)
        String email;

        @NotNull(message = INVITE_ASSESSMENT_USER_ROLE_ID_NOT_NULL)
        Integer roleId;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        public Param(UUID assessmentId, String email, Integer roleId, UUID currentUserId) {
            this.assessmentId = assessmentId;
            this.email = email;
            this.roleId = roleId;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }
}
