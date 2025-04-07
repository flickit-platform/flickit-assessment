package org.flickit.assessment.kit.application.port.in.measure;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.kit.application.domain.Measure;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;
import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_MEASURES_SIZE_MAX;

public interface GetMeasuresUseCase {

    PaginatedResponse<MeasureListItem> getMeasures(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<GetMeasuresUseCase.Param> {

        @NotNull(message = GET_MEASURES_KIT_VERSION_ID_NOT_NULL)
        Long kitVersionId;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Min(value = 0, message = GET_MEASURES_PAGE_MIN)
        int page;

        @Min(value = 1, message = GET_MEASURES_SIZE_MIN)
        @Max(value = 100, message = GET_MEASURES_SIZE_MAX)
        int size;

        @Builder
        public Param(Long kitVersionId, UUID currentUserId, int page, int size) {
            this.kitVersionId = kitVersionId;
            this.currentUserId = currentUserId;
            this.page = page;
            this.size = size;
            this.validateSelf();
        }
    }

    record MeasureListItem(Measure measure, int questionsCount) {
    }
}


