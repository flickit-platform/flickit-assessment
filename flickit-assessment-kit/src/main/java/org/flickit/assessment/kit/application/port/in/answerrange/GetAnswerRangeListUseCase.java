package org.flickit.assessment.kit.application.port.in.answerrange;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;
import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_ANSWER_RANGE_LIST_PAGE_MIN;

public interface GetAnswerRangeListUseCase {

    PaginatedResponse<AnswerRangeItemList> getAnswerRangeList(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = GET_ANSWER_RANGE_LIST_KIT_VERSION_ID_NOT_NULL)
        Long kitVersionId;

        @Min(value = 0, message = GET_ANSWER_RANGE_LIST_PAGE_MIN)
        int page;

        @Min(value = 1, message = GET_ANSWER_RANGE_LIST_SIZE_MIN)
        @Max(value = 100, message = GET_ANSWER_RANGE_LIST_SIZE_MAX)
        int size;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Builder
        public Param(Long kitVersionId, int page, int size, UUID currentUserId) {
            this.kitVersionId = kitVersionId;
            this.page = page;
            this.size = size;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }

    record AnswerRangeItemList(Long id, String title, List<AnswerOptionListItem> answerOptions) {

        public record AnswerOptionListItem(long id, String title, int index) {
        }
    }
}
