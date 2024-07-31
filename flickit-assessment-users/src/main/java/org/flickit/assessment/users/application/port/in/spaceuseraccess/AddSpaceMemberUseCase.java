package org.flickit.assessment.users.application.port.in.spaceuseraccess;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.UUID;

import static org.flickit.assessment.users.common.ErrorMessageKey.ADD_SPACE_MEMBER_SPACE_ID_NOT_NULL;
import static org.flickit.assessment.users.common.ErrorMessageKey.ADD_SPACE_MEMBER_EMAIL_NOT_NULL;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;

public interface AddSpaceMemberUseCase {

    void addMember(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param>{

        @NotNull(message = ADD_SPACE_MEMBER_SPACE_ID_NOT_NULL)
        Long spaceId;

        @NotBlank(message = ADD_SPACE_MEMBER_EMAIL_NOT_NULL)
        String email;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        public Param(Long spaceId, String email, UUID currentUserId) {
            this.spaceId = spaceId;
            this.email = email != null ? email.strip().toLowerCase() : null;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }
}
