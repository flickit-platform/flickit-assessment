package org.flickit.assessment.users.application.port.in.expertgroupaccess;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.users.application.domain.ExpertGroupAccessStatus;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.users.common.ErrorMessageKey.*;

public interface GetExpertGroupMembersUseCase {

    PaginatedResponse<Member> getExpertGroupMembers(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = GET_EXPERT_GROUP_MEMBERS_ID_NOT_NULL)
        Long id;

        @Nullable
        ExpertGroupAccessStatus status;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Min(value = 1, message = GET_EXPERT_GROUP_MEMBERS_SIZE_MIN)
        @Max(value = 100, message = GET_EXPERT_GROUP_MEMBERS_SIZE_MAX)
        int size;

        @Min(value = 0, message = GET_EXPERT_GROUP_MEMBERS_PAGE_MIN)
        int page;

        public Param(long expertGroupId, ExpertGroupAccessStatus status, UUID currentUserId, int size, int page) {
            this.id = expertGroupId;
            this.status = status;
            this.currentUserId = currentUserId;
            this.size = size;
            this.page = page;
            this.validateSelf();
        }
    }

    record Member(
        UUID id,
        String email,
        String displayName,
        String bio,
        String pictureLink,
        String linkedin,
        ExpertGroupAccessStatus status,
        LocalDateTime inviteExpirationDate) {
    }
}
