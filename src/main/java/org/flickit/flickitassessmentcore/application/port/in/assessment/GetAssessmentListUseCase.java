package org.flickit.flickitassessmentcore.application.port.in.assessment;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.flickitassessmentcore.application.domain.AssessmentColor;
import org.flickit.flickitassessmentcore.common.SelfValidating;
import org.flickit.flickitassessmentcore.application.domain.crud.PaginatedResponse;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.*;

public interface GetAssessmentListUseCase {

    PaginatedResponse<AssessmentListItem> getAssessmentList(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = GET_ASSESSMENT_LIST_SPACE_ID_NOT_NULL)
        Long spaceId;

        @Min(value = 1, message = GET_ASSESSMENT_LIST_SIZE_MIN)
        @Max(value = 100, message = GET_ASSESSMENT_LIST_SIZE_MAX)
        int size;

        @Min(value = 0, message = GET_ASSESSMENT_LIST_PAGE_MIN)
        int page;

        public Param(Long spaceId, int size, int page) {
            this.spaceId = spaceId;
            this.size = size;
            this.page = page;
            this.validateSelf();
        }
    }

    record AssessmentListItem(
        UUID id,
        String title,
        Long assessmentKitId,
        AssessmentColor color,
        LocalDateTime lastModificationTime,
        Long maturityLevelId,
        boolean isCalculateValid
    ) {
    }
}
