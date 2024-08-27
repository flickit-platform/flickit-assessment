package org.flickit.assessment.core.application.port.in.subjectinsight;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;

public interface CreateSubjectInsightUseCase {

    void createSubjectInsight(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = CREATE_SUBJECT_INSIGHT_ASSESSMENT_ID_NOT_NULL)
        UUID assessmentId;

        @NotNull(message = CREATE_SUBJECT_INSIGHT_SUBJECT_ID_NOT_NULL)
        Long subjectId;

        @NotBlank(message = CREATE_SUBJECT_INSIGHT_INSIGHT_NOT_NULL)
        @Size(min = 3, message = CREATE_SUBJECT_INSIGHT_INSIGHT_SIZE_MIN)
        @Size(max = 1000, message = CREATE_SUBJECT_INSIGHT_INSIGHT_SIZE_MAX)
        String insight;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        public Param(UUID assessmentId, Long subjectId, String insight, UUID currentUserId) {
            this.assessmentId = assessmentId;
            this.subjectId = subjectId;
            this.insight = insight != null ? insight.strip() : null;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }
}
