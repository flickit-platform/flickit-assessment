package org.flickit.assessment.core.application.port.in.attribute;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;

public interface GetAttributeScoreStatsUseCase {

    Result getAttributeScoreStats(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = GET_ATTRIBUTE_SCORE_STATS_ASSESSMENT_ID_NOT_NULL)
        UUID assessmentId;

        @NotNull(message = GET_ATTRIBUTE_SCORE_STATS_ATTRIBUTE_ID_NOT_NULL)
        Long attributeId;

        @NotNull(message = GET_ATTRIBUTE_SCORE_STATS_MATURITY_LEVEL_ID_NOT_NULL)
        Long maturityLevelId;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        public Param(UUID assessmentId, Long attributeId, Long maturityLevelId, UUID currentUserId) {
            this.assessmentId = assessmentId;
            this.attributeId = attributeId;
            this.maturityLevelId = maturityLevelId;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }

    record Result(double maxPossibleScore,
                  double gainedScore,
                  double gainedScorePercentage,
                  int questionsCount) {
    }

}
