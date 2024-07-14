package org.flickit.assessment.core.application.port.in.assessmentinvitee;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.UUID;

import static org.flickit.assessment.core.common.ErrorMessageKey.*;

public interface InviteAssessmentUserUseCase {

    void inviteUser(UUID assessmentId, String email, Integer roleId);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = INVITE_ASSESSMENT_USER_ASSESSMENT_ID_NOT_NULL)
        UUID assessmentId;

        @NotNull(message = INVITE_ASSESSMENT_USER_EMAIL_NOT_NULL)
        String email;

        @NotNull(message = INVITE_ASSESSMENT_USER_ROLE_ID_NOT_NULL)
        Integer roleId;

        public Param(UUID assessmentId, String email, Integer roleId) {
            this.assessmentId = assessmentId;
            this.email = email;
            this.roleId = roleId;
            this.validateSelf();
        }
    }
}
