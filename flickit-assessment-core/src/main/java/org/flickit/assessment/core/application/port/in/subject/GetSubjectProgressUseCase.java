package org.flickit.assessment.core.application.port.in.subject;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_SUBJECT_PROGRESS_ASSESSMENT_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_SUBJECT_PROGRESS_SUBJECT_ID_NOT_NULL;

public interface GetSubjectProgressUseCase {

    Result getSubjectProgress(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = GET_SUBJECT_PROGRESS_ASSESSMENT_ID_NOT_NULL)
        UUID assessmentId;

        @NotNull(message = GET_SUBJECT_PROGRESS_SUBJECT_ID_NOT_NULL)
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

    record Result(Long id, String title, Integer questionCount, Integer answerCount) {
    }
}
