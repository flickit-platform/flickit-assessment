package org.flickit.assessment.core.application.port.in.assessment;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.MIGRATE_ASSESSMENT_RESULT_KIT_VERSION_ASSESSMENT_ID_NOT_NULL;

public interface MigrateAssessmentResultKitVersionUseCase {

    void migrateKitVersion(Param param);

    @Value
    @EqualsAndHashCode(callSuper = true)
    class Param extends SelfValidating<Param> {

        @NotNull(message = MIGRATE_ASSESSMENT_RESULT_KIT_VERSION_ASSESSMENT_ID_NOT_NULL)
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
}
