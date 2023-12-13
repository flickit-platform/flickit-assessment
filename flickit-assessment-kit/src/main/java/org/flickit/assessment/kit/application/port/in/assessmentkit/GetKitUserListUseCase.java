package org.flickit.assessment.kit.application.port.in.assessmentkit;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.kit.application.domain.crud.PaginatedResponse;

import static org.flickit.assessment.kit.common.ErrorMessageKey.*;

public interface GetKitUserListUseCase {

    PaginatedResponse<KitUserListItem> getKitUserList(Param param);

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

        public Param(Long kitId, int page, int size) {
            this.kitId = kitId;
            this.page = page;
            this.size = size;
            this.validateSelf();
        }
    }

    record KitUserListItem(
        String name,
        String email) {
    }
}
