package org.flickit.assessment.core.application.port.in.subjectinsight;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.UUID;

import static org.flickit.assessment.core.common.ErrorMessageKey.INIT_SUBJECT_INSIGHT_ASSESSMENT_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.INIT_SUBJECT_INSIGHT_SUBJECT_ID_NOT_NULL;

public interface InitSubjectInsightUseCase {

    void initSubjectInsight(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = INIT_SUBJECT_INSIGHT_ASSESSMENT_ID_NOT_NULL)
        UUID assessmentId;

        @NotNull(message = INIT_SUBJECT_INSIGHT_SUBJECT_ID_NOT_NULL)
        Long subjectId;

        @Builder
        public Param(UUID assessmentId, Long subjectId) {
            this.assessmentId = assessmentId;
            this.subjectId = subjectId;
            this.validateSelf();
        }
    }
}
