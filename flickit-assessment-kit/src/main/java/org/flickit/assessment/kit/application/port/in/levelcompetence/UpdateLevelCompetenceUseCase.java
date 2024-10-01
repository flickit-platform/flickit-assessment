package org.flickit.assessment.kit.application.port.in.levelcompetence;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.UUID;

import static org.flickit.assessment.kit.common.ErrorMessageKey.*;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;

public interface UpdateLevelCompetenceUseCase {

    void updateLevelCompetence(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = UPDATE_LEVEL_COMPETENCE_LEVEL_COMPETENCE_ID_NOT_NULL)
        Long levelCompetenceId;

        @NotNull(message = UPDATE_LEVEL_COMPETENCE_KIT_VERSION_ID_NOT_NULL)
        Long kitVersionId;

        @NotNull(message = UPDATE_LEVEL_COMPETENCE_VALUE_NOT_NULL)
        Integer value;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Builder
        public Param(Long levelCompetenceId, Long kitVersionId, Integer value, UUID currentUserId) {
            this.levelCompetenceId = levelCompetenceId;
            this.kitVersionId = kitVersionId;
            this.value = value;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }
}
