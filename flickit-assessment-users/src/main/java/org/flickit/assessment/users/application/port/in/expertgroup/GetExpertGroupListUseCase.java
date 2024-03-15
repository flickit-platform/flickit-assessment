package org.flickit.assessment.users.application.port.in.expertgroup;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.users.common.ErrorMessageKey.*;

public interface GetExpertGroupListUseCase {

    PaginatedResponse<ExpertGroupListItem> getExpertGroupList(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @Min(value = 1, message = GET_EXPERT_GROUP_LIST_SIZE_MIN)
        @Max(value = 100, message = GET_EXPERT_GROUP_LIST_SIZE_MAX)
        int size;

        @Min(value = 0, message = GET_EXPERT_GROUP_LIST_PAGE_MIN)
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

    record ExpertGroupListItem(Long id, String title, String bio, String picture, Integer publishedKitsCount,
                               Integer membersCount, List<Member> members, Boolean editable) {
    }

    record Member(String displayName) {
    }
}
