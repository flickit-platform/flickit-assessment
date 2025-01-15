package org.flickit.assessment.core.application.port.in.assessment;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.core.application.domain.AssessmentListItem;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;

public interface GetSpaceAssessmentListUseCase {

    PaginatedResponse<SpaceAssessmentListItem> getAssessmentList(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = GET_SPACE_ASSESSMENT_LIST_SPACE_ID_NOT_NULL)
        Long spaceId;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Min(value = 1, message = GET_SPACE_ASSESSMENT_LIST_SIZE_MIN)
        @Max(value = 100, message = GET_SPACE_ASSESSMENT_LIST_SIZE_MAX)
        int size;

        @Min(value = 0, message = GET_SPACE_ASSESSMENT_LIST_PAGE_MIN)
        int page;

        public Param(Long spaceId, UUID currentUserId, int size, int page) {
            this.spaceId = spaceId;
            this.currentUserId = currentUserId;
            this.size = size;
            this.page = page;
            this.validateSelf();
        }
    }

    record SpaceAssessmentListItem(UUID id,
                                   String title,
                                   AssessmentListItem.Kit kit,
                                   LocalDateTime lastModificationTime,
                                   AssessmentListItem.MaturityLevel maturityLevel,
                                   Double confidenceValue,
                                   boolean isCalculateValid,
                                   boolean isConfidenceValid,
                                   Permissions permissions) {

        public record Permissions(boolean canManageSettings,
                                  boolean canViewReport,
                                  boolean canViewDashboard,
                                  boolean canViewQuestionnaires) {
        }
    }
}
