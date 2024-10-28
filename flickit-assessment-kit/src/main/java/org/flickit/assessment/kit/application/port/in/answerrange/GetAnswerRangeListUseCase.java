package org.flickit.assessment.kit.application.port.in.answerrange;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_ANSWER_RANGE_LIST_KIT_VERSION_ID_NOT_NULL;

public interface GetAnswerRangeListUseCase {

    PaginatedResponse<AnswerRangeListItem> getAnswerRangeList(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = GET_ANSWER_RANGE_LIST_KIT_VERSION_ID_NOT_NULL )
        Long kitVersionId;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Builder
        public Param(Long kitVersionId, UUID currentUserId) {
            this.kitVersionId = kitVersionId;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }

    record AnswerRangeListItem(long id, String title, List<AnswerOption> AnswerOption){
        public record AnswerOption(long id, String title, int index) {
        }
    }
}
