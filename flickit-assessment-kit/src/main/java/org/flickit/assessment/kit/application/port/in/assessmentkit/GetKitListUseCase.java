package org.flickit.assessment.kit.application.port.in.assessmentkit;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.kit.application.domain.KitTag;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;

public interface GetKitListUseCase {

    PaginatedResponse<KitListItem> getKitList(Param param);

    @Value
    @EqualsAndHashCode(callSuper = true)
    class Param extends SelfValidating<Param> {

        @NotNull(message = GET_KIT_LIST_IS_PRIVATE_NOT_NULL)
        Boolean isPrivate;

        @Min(value = 0, message = GET_KIT_LIST_PAGE_MIN)
        int page;

        @Min(value = 1, message = GET_KIT_LIST_SIZE_MIN)
        @Max(value = 100, message = GET_KIT_LIST_SIZE_MAX)
        int size;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        public Param(Boolean isPrivate, int page, int size, UUID currentUserId) {
            this.isPrivate = isPrivate;
            this.page = page;
            this.size = size;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }

    record KitListItem(
        long id,
        String title,
        String summary,
        boolean isPrivate,
        int likes,
        int assessmentsCount,
        ExpertGroup expertGroup,
        List<KitTag> tags) {

        public record ExpertGroup(
            long id,
            String title,
            String picture) {
        }
    }
}
