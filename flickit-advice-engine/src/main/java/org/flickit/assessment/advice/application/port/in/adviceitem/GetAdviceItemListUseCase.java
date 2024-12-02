package org.flickit.assessment.advice.application.port.in.adviceitem;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;

import java.util.UUID;

import static org.flickit.assessment.advice.common.ErrorMessageKey.*;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;

public interface GetAdviceItemListUseCase {

    PaginatedResponse<AdviceItemListItem> getAdviceItems(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = GET_ADVICE_ITEM_LIST_ASSESSMENT_ID_NOT_NULL)
        UUID assessmentId;

        @Min(value = 1, message = GET_ADVICE_ITEM_LIST_SIZE_MIN)
        @Max(value = 100, message = GET_ADVICE_ITEM_LIST_SIZE_MAX)
        int size;

        @Min(value = 0, message = GET_ADVICE_ITEM_LIST_PAGE_MIN)
        int page;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Builder
        public Param(UUID assessmentId, int size, int page, UUID currentUserId) {
            this.assessmentId = assessmentId;
            this.size = size;
            this.page = page;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }

    record AdviceItemListItem(UUID id,
                              String title,
                              String description,
                              String cost,
                              String priority,
                              String impact) {
    }
}
