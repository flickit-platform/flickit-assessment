package org.flickit.assessment.core.application.port.in.subjectinsight;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.core.application.port.in.subject.ReportSubjectUseCase;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.CREATE_SUBJECT_INSIGHT_ASSESSMENT_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.CREATE_SUBJECT_INSIGHT_SUBJECT_ID_NOT_NULL;

public interface CreateSubjectInsightUseCase {

    Result createSubjectInsight(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<ReportSubjectUseCase.Param> {

        @NotNull(message = CREATE_SUBJECT_INSIGHT_ASSESSMENT_ID_NOT_NULL)
        UUID assessmentId;

        @NotNull(message = CREATE_SUBJECT_INSIGHT_SUBJECT_ID_NOT_NULL)
        Long subjectId;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        public Param(UUID assessmentId, Long subjectId, UUID currentUserId) {
            this.assessmentId = assessmentId;
            this.subjectId = subjectId;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }

    record Result(UUID id) {
    }
}
