package org.flickit.assessment.kit.application.port.in.assessmentkit;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.time.LocalDateTime;
import java.util.List;

import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_KIT_STATS_KIT_ID_NOT_NULL;

public interface GetKitStatsUseCase {

    Result getKitStats(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = GET_KIT_STATS_KIT_ID_NOT_NULL)
        Long assessmentKitId;

        public Param(Long assessmentKitId) {
            this.assessmentKitId = assessmentKitId;
            this.validateSelf();
        }
    }

    record Result(
        LocalDateTime creationTime,
        LocalDateTime lastUpdateTime,
        Long questionnairesCount,
        Long attributesCount,
        Long questionsCount,
        Long maturityLevelsCount,
        Long likes,
        Long assessmentCounts,
        List<KitStatSubject> subjects,
        KitStatExpertGroup expertGroup
    ) {
    }

    record KitStatSubject(String title) {}

    record KitStatExpertGroup(Long id, String name) {}
}
