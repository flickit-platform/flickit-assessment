package org.flickit.assessment.core.application.port.in.assessmentinvitee;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.UUID;

import static org.flickit.assessment.core.common.ErrorMessageKey.*;

public interface InviteAssessmentUserUseCase {

    void inviteUser(UUID assessmentId, UUID userId, Integer roleId);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = INVITE_ASSESSMENT_USER_ASSESSMENT_ID_NOT_NULL)
        UUID assessmentId;

        @NotNull(message = INVITE_ASSESSMENT_USER_USER_ID_NOT_NULL)
        UUID userId;

        @NotNull(message = INVITE_ASSESSMENT_USER_ROLE_ID_NOT_NULL)
        Integer roleId;

        public Param(UUID assessmentId, UUID userId, Integer roleId) {
            this.assessmentId = assessmentId;
            this.userId = userId;
            this.roleId = roleId;
            this.validateSelf();
        }
    }
}
