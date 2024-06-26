package org.flickit.assessment.users.application.port.in.space;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.users.common.ErrorMessageKey.*;

public interface GetSpaceListUseCase {

    PaginatedResponse<SpaceListItem> getSpaceList(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @Min(value = 1, message = GET_SPACE_LIST_SIZE_MIN)
        @Max(value = 100, message = GET_SPACE_LIST_SIZE_MAX)
        int size;

        @Min(value = 0, message = GET_SPACE_LIST_PAGE_MIN)
        int page;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        public Param(int size, int page, UUID currentUserId) {
            this.size = size;
            this.page = page;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }

    record SpaceListItem(long id, String title, Owner owner, LocalDateTime lastModificationTime,
                         int membersCount, int assessmentsCount) {

        public record Owner(UUID id, String displayName, Boolean isCurrentUserOwner) {}
    }
}
