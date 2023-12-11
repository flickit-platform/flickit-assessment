package org.flickit.assessment.kit.application.port.in.user;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.kit.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.kit.common.SelfValidating;

import static org.flickit.assessment.kit.common.ErrorMessageKey.*;

public interface GetUserListUseCase {

    PaginatedResponse<UserListItem> getUserList(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = GET_USER_LIST_KIT_ID_NOT_NULL)
        Long kitId;

        @Min(value = 0, message = GET_USER_LIST_PAGE_MIN)
        int page;

        @Min(value = 1, message = GET_USER_LIST_SIZE_MIN)
        @Max(value = 100, message = GET_USER_LIST_SIZE_MAX)
        int size;

        public Param(Long kitId, int page, int size) {
            this.kitId = kitId;
            this.page = page;
            this.size = size;
            this.validateSelf();
        }
    }

    record UserListItem(
        String name,
        String email) {
    }
}
