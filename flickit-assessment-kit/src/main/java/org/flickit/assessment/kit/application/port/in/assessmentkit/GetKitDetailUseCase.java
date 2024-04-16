package org.flickit.assessment.kit.application.port.in.assessmentkit;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_KIT_DETAIL_KIT_ID_NOT_NULL;

public interface GetKitDetailUseCase {

    Result getKitDetail(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = GET_KIT_DETAIL_KIT_ID_NOT_NULL)
        Long kitId;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        public Param(Long kitId, UUID currentUserId) {
            this.kitId = kitId;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }

    record Result(
        List<KitDetailMaturityLevel> maturityLevels,
        List<KitDetailSubject> subjects,
        List<KitDetailQuestionnaire> questionnaires) {
    }

    record KitDetailMaturityLevel(long id, String title, int index, List<Competences> competences) {
    }

    record Competences(String title, int value, long maturityLevelId) {
    }

    record KitDetailSubject(long id, String title, int index) {
    }

    record KitDetailQuestionnaire(long id, String title, int index) {
    }
}
