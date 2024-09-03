package org.flickit.assessment.core.application.port.in.assessmentanalysis;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.CREATE_ASSESSMENT_AI_ANALYSIS_ASSESSMENT_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.CREATE_ASSESSMENT_AI_ANALYSIS_TYPE_NOT_NULL;

public interface CreateAssessmentAnalysisUseCase {

    void createAiAnalysis(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = CREATE_ASSESSMENT_AI_ANALYSIS_ASSESSMENT_ID_NOT_NULL)
        UUID assessmentId;

        @NotNull(message = CREATE_ASSESSMENT_AI_ANALYSIS_TYPE_NOT_NULL)
        Integer type;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        public Param(UUID assessmentId, Integer type, UUID currentUserId) {

            this.assessmentId = assessmentId;
            this.type = type;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }

    record Result(String analysis) {
    }
}
