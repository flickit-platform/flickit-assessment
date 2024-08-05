package org.flickit.assessment.core.application.port.in.assessmentinvite;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.UPDATE_ASSESSMENT_INVITE_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.UPDATE_ASSESSMENT_INVITE_ROLE_ID_NOT_NULL;

public interface UpdateAssessmentInviteUseCase {

    void updateInvite(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = UPDATE_ASSESSMENT_INVITE_ID_NOT_NULL)
        UUID inviteId;

        @NotNull(message = UPDATE_ASSESSMENT_INVITE_ROLE_ID_NOT_NULL)
        Integer roleId;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        public Param(UUID inviteId, Integer roleId, UUID currentUserId) {
            this.inviteId = inviteId;
            this.roleId = roleId;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }
}
