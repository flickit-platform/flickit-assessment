package org.flickit.assessment.kit.application.port.in.assessmentkit;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;

public interface GetKitUserListUseCase {

    PaginatedResponse<UserListItem> getKitUserList(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = GET_KIT_USER_LIST_KIT_ID_NOT_NULL)
        Long kitId;

        @Min(value = 0, message = GET_KIT_USER_LIST_PAGE_MIN)
        int page;

        @Min(value = 1, message = GET_KIT_USER_LIST_SIZE_MIN)
        @Max(value = 100, message = GET_KIT_USER_LIST_SIZE_MAX)
        int size;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Builder
        public Param(Long kitId, int page, int size, UUID currentUserId) {
            this.kitId = kitId;
            this.page = page;
            this.size = size;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }

    record UserListItem(UUID id, String name, String email, String pictureLink, boolean editable) {
    }

}
