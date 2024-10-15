package org.flickit.assessment.kit.application.port.in.assessmentkit;

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
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;

public interface GetExpertGroupKitListUseCase {

    PaginatedResponse<Result> getExpertGroupKitList(Param param);

    @Value
    @EqualsAndHashCode(callSuper = true)
    class Param extends SelfValidating<Param> {

        @NotNull(message = GET_EXPERT_GROUP_KIT_LIST_EXPERT_GROUP_ID_NOT_NULL)
        Long expertGroupId;

        @Min(value = 0, message = GET_EXPERT_GROUP_KIT_LIST_PAGE_MIN)
        int page;

        @Min(value = 1, message = GET_EXPERT_GROUP_KIT_LIST_SIZE_MIN)
        @Max(value = 100, message = GET_EXPERT_GROUP_KIT_LIST_SIZE_MAX)
        int size;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        public Param(Long expertGroupId, int page, int size, UUID currentUserId) {
            this.expertGroupId = expertGroupId;
            this.page = page;
            this.size = size;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }

    record Result(
        Long id,
        String title,
        Boolean published,
        Boolean isPrivate,
        LocalDateTime lastModificationTime,
        Long draftVersionId) {
    }
}
