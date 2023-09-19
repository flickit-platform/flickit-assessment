package org.flickit.flickitassessmentcore.application.port.in.assessment;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.flickitassessmentcore.application.domain.crud.PaginatedResponse;
import org.flickit.flickitassessmentcore.common.SelfValidating;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.*;

public interface GetComparableAssessmentsUseCase {

    PaginatedResponse<AssessmentListItem> getComparableAssessments(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotEmpty(message = GET_COMPARABLE_ASSESSMENTS_SPACE_IDS_NOT_NULL)
        List<Long> spaceIds;

        Long kitId;

        @Min(value = 1, message = GET_COMPARABLE_ASSESSMENTS_SIZE_MIN)
        @Max(value = 100, message = GET_COMPARABLE_ASSESSMENTS_SIZE_MAX)
        int size;

        @Min(value = 0, message = GET_COMPARABLE_ASSESSMENTS_PAGE_MIN)
        int page;

        public Param(List<Long> spaceIds, Long kitId, int size, int page) {
            this.spaceIds = spaceIds;
            this.kitId = kitId;
            this.size = size;
            this.page = page;
            this.validateSelf();
        }
    }

    record AssessmentListItem(
        UUID id,
        String title,
        Long assessmentKitId,
        Long spaceId,
        int colorId,
        LocalDateTime lastModificationTime,
        Long maturityLevelId,
        boolean isCalculateValid
    ) {
    }
}
