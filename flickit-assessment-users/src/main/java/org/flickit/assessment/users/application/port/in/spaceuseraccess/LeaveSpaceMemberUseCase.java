package org.flickit.assessment.users.application.port.in.spaceuseraccess;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.users.application.port.in.expertgroup.DeleteExpertGroupUseCase;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.users.common.ErrorMessageKey.LEAVE_SPACE_MEMBER_SPACE_ID_NOT_NULL;

public interface LeaveSpaceMemberUseCase {

    void leaveMember(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<DeleteExpertGroupUseCase.Param> {

        @NotNull(message = LEAVE_SPACE_MEMBER_SPACE_ID_NOT_NULL)
        Long id;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        public Param(Long id, UUID currentUserId) {
            this.id = id;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }
}
