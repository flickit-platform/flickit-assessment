package org.flickit.assessment.kit.application.port.in.levelcompetence;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;

public interface CreateLevelCompetenceUseCase {

    void createLevelCompetence(Param param);

    @Value
    @EqualsAndHashCode(callSuper = true)
    class Param extends SelfValidating<Param> {

        @NotNull(message = CREATE_LEVEL_COMPETENCE_KIT_VERSION_ID_NOT_NULL)
        Long kitVersionId;

        @NotNull(message = CREATE_LEVEL_COMPETENCE_AFFECTED_LEVEL_ID_NOT_NULL)
        Long affectedLevelId;

        @NotNull(message = CREATE_LEVEL_COMPETENCE_EFFECTIVE_LEVEL_ID_NOT_NULL)
        Long effectiveLevelId;

        @NotNull(message = CREATE_LEVEL_COMPETENCE_VALUE_NOT_NULL)
        @Min(value = 1, message = CREATE_LEVEL_COMPETENCE_VALUE_MIN)
        @Max(value = 100, message = CREATE_LEVEL_COMPETENCE_VALUE_MAX)
        Integer value;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        public Param(Long kitVersionId, Long affectedLevelId, Long effectiveLevelId, Integer value, UUID currentUserId) {
            this.kitVersionId = kitVersionId;
            this.affectedLevelId = affectedLevelId;
            this.effectiveLevelId = effectiveLevelId;
            this.value = value;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }
}
