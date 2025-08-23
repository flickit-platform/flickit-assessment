package org.flickit.assessment.kit.application.port.in.kitversion;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_KIT_VERSION_KIT_VERSION_ID_NOT_NULL;

public interface GetKitVersionUseCase {

    Result getKitVersion(Param param);

    @Value
    @EqualsAndHashCode(callSuper = true)
    class Param extends SelfValidating<Param> {

        @NotNull(message = GET_KIT_VERSION_KIT_VERSION_ID_NOT_NULL)
        Long kitVersionId;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Builder
        public Param(Long kitVersionId, UUID currentUserId) {
            this.kitVersionId = kitVersionId;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }

    record Result(Long id, LocalDateTime creationTime, AssessmentKit assessmentKit) {

        public record AssessmentKit(Long id, String title, ExpertGroup expertGroup) {
        }

        public record ExpertGroup(Long id, String title) {
        }
    }
}
