package org.flickit.assessment.core.application.port.in.assessmentinvitee;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;

public interface GetAssessmentInviteeListUseCase {

    PaginatedResponse<Result> getInvitees(Param param);

    @Value
    @EqualsAndHashCode(callSuper = true)
    class Param extends SelfValidating<Param> {

        @NotNull(message = GET_ASSESSMENT_INVITEE_LIST_ASSESSMENT_ID_NOT_NULL)
        UUID assessmentId;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Min(value = 1, message = GET_ASSESSMENT_INVITEE_LIST_SIZE_MIN)
        @Max(value = 100, message = GET_ASSESSMENT_INVITEE_LIST_SIZE_MAX)
        int size;

        @Min(value = 0, message = GET_ASSESSMENT_INVITEE_LIST_PAGE_MIN)
        int page;

        public Param(UUID assessmentId, UUID currentUserId, int size, int page) {
            this.assessmentId = assessmentId;
            this.currentUserId = currentUserId;
            this.size = size;
            this.page = page;
            this.validateSelf();
        }
    }

    record Result(UUID id,
                  String email,
                  Role role,
                  LocalDateTime expirationTime,
                  LocalDateTime creationTime
    ) {

        public record Role(int id,
                           String title) {
        }
    }
}
