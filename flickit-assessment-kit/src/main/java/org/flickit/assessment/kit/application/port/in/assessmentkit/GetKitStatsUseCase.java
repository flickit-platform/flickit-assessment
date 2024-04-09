package org.flickit.assessment.kit.application.port.in.assessmentkit;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_KIT_STATS_KIT_ID_NOT_NULL;

public interface GetKitStatsUseCase {

    Result getKitStats(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = GET_KIT_STATS_KIT_ID_NOT_NULL)
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
        LocalDateTime creationTime,
        LocalDateTime lastModificationTime,
        Integer questionnairesCount,
        Integer attributesCount,
        Integer questionsCount,
        Integer maturityLevelsCount,
        Integer likes,
        Integer assessmentCounts,
        List<KitStatSubject> subjects,
        KitStatExpertGroup expertGroup) {
    }

    record KitStatSubject(String title) {}

    record KitStatExpertGroup(Long id, String title) {}
}
