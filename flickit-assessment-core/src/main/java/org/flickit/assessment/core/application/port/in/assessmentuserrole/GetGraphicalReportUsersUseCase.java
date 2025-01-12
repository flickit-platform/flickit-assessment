package org.flickit.assessment.core.application.port.in.assessmentuserrole;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_GRAPHICAL_REPORT_USERS_ASSESSMENT_ID_NOT_NULL;

public interface GetGraphicalReportUsersUseCase {

    Result getGraphicalReportUsers(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = GET_GRAPHICAL_REPORT_USERS_ASSESSMENT_ID_NOT_NULL)
        UUID assessmentId;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Builder
        public Param(UUID assessmentId, UUID currentUserId) {
            this.assessmentId = assessmentId;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }

    record Result(List<GraphicalReportUser> users, List<GraphicalReportInvitee> invitees) {

        public record GraphicalReportUser(UUID id, String email, String displayName, String pictureLink) {}

        public record GraphicalReportInvitee(String email) {}
    }
}
