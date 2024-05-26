package org.flickit.assessment.users.application.port.in.spaceuseraccess;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.users.common.ErrorMessageKey.*;

public interface GetSpaceMembersUseCase {

    PaginatedResponse<Member> getSpaceMembers(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = GET_SPACE_MEMBERS_SPACE_ID_NOT_NULL)
        Long id;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Min(value = 1, message = GET_SPACE_MEMBERS_SIZE_MIN)
        @Max(value = 100, message = GET_SPACE_MEMBERS_SIZE_MAX)
        int size;

        @Min(value = 0, message = GET_SPACE_MEMBERS_PAGE_MIN)
        int page;

        public Param(Long spaceId, UUID currentUserId, int size, int page) {
            this.id = spaceId;
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
        boolean isOwner,
        String pictureLink,
        String linkedin) {
    }
}
