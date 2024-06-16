package org.flickit.assessment.core.application.port.in.assessment;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.core.application.domain.AssessmentListItem;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;

public interface GetAssessmentListUseCase {

    PaginatedResponse<AssessmentListItem> getAssessmentList(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        Long kitId;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Min(value = 1, message = GET_ASSESSMENT_LIST_SIZE_MIN)
        @Max(value = 100, message = GET_ASSESSMENT_LIST_SIZE_MAX)
        int size;

        @Min(value = 0, message = GET_ASSESSMENT_LIST_PAGE_MIN)
        int page;

        public Param(Long kitId, UUID currentUserId, int size, int page) {
            this.kitId = kitId;
            this.currentUserId = currentUserId;
            this.size = size;
            this.page = page;
            this.validateSelf();
        }
    }
}
