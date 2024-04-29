package org.flickit.assessment.core.application.port.in.assessment;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.core.application.domain.AssessmentColor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.core.common.ErrorMessageKey.*;

public interface GetAssessmentListUseCase {

    PaginatedResponse<AssessmentListItem> getAssessmentList(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotEmpty(message = GET_ASSESSMENT_LIST_SPACE_IDS_NOT_NULL)
        List<Long> spaceIds;

        Long kitId;

        @Min(value = 1, message = GET_ASSESSMENT_LIST_SIZE_MIN)
        @Max(value = 100, message = GET_ASSESSMENT_LIST_SIZE_MAX)
        int size;

        @Min(value = 0, message = GET_ASSESSMENT_LIST_PAGE_MIN)
        int page;

        public Param(List<Long> spaceIds, Long kitId, int size, int page) {
            this.spaceIds = spaceIds;
            this.kitId = kitId;
            this.size = size;
            this.page = page;
            this.validateSelf();
        }
    }

    record AssessmentListItem(UUID id,
                              String title,
                              Kit kit,
                              Space space,
                              AssessmentColor color,
                              LocalDateTime lastModificationTime,
                              MaturityLevel maturityLevel,
                              boolean isCalculateValid,
                              boolean isConfidenceValid) {

        public record Kit(long id, String title, int maturityLevelsCount) {}

        public record Space(long id, String title) {}

        public record MaturityLevel(long id, String title, int value, int index) {}
    }
}
