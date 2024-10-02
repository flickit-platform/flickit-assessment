package org.flickit.assessment.kit.application.port.in.levelcompetence;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.DELETE_LEVEL_COMPETENCE_KIT_VERSION_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.DELETE_LEVEL_COMPETENCE_LEVEL_COMPETENCE_ID_NOT_NULL;

public interface DeleteLevelCompetenceUseCase {

    void deleteLevelCompetence(Param param);

    @Value
    @EqualsAndHashCode(callSuper = true)
    class Param extends SelfValidating<Param> {

        @NotNull(message = DELETE_LEVEL_COMPETENCE_KIT_VERSION_ID_NOT_NULL)
        Long kitVersionId;

        @NotNull(message = DELETE_LEVEL_COMPETENCE_LEVEL_COMPETENCE_ID_NOT_NULL)
        Long levelCompetenceId;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Builder
        public Param(Long kitVersionId, Long levelCompetenceId, UUID currentUserId) {
            this.kitVersionId = kitVersionId;
            this.levelCompetenceId = levelCompetenceId;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }
}