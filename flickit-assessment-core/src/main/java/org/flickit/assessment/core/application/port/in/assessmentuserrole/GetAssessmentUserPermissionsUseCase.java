package org.flickit.assessment.core.application.port.in.assessmentuserrole;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.Map;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_ASSESSMENT_USER_PERMISSIONS_ASSESSMENT_ID_NOT_NULL;

public interface GetAssessmentUserPermissionsUseCase {

    Result getAssessmentUserPermissions(Param param);

    @Value
    @EqualsAndHashCode(callSuper = true)
    class Param extends SelfValidating<Param> {

        @NotNull(message = GET_ASSESSMENT_USER_PERMISSIONS_ASSESSMENT_ID_NOT_NULL)
        UUID assessmentId;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID userId;

        public Param(@NotNull UUID assessmentId, @NotNull UUID userId) {
            this.assessmentId = assessmentId;
            this.userId = userId;
            this.validateSelf();
        }
    }

    record Result(Map<String, Boolean> permissions) {
    }
}
