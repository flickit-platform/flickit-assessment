package org.flickit.assessment.kit.application.port.in.levelcompetence;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_LEVEL_COMPETENCES_KIT_VERSION_ID_NOT_NULL;

public interface GetLevelCompetencesUseCase {

    List<MaturityLevelListItem> getLevelCompetences(Param param);

    @Value
    @EqualsAndHashCode(callSuper = true)
    class Param extends SelfValidating<Param> {

        @NotNull(message = GET_LEVEL_COMPETENCES_KIT_VERSION_ID_NOT_NULL)
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

    record MaturityLevelListItem(long id, int index, String title, List<Competence> competences) {
    }

    record Competence(long id, int value, long maturityLevelId) {
    }
}
