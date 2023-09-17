package org.flickit.flickitassessmentcore.application.port.in.subject;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.flickitassessmentcore.common.SelfValidating;

import java.util.UUID;

import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.GET_SUBJECT_PROGRESS_ASSESSMENT_ID_NOT_NULL;
import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.GET_SUBJECT_PROGRESS_SUBJECT_ID_NOT_NULL;

public interface GetSubjectProgressUseCase {

    Result getSubjectProgress(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = GET_SUBJECT_PROGRESS_ASSESSMENT_ID_NOT_NULL)
        UUID assessmentId;

        @NotNull(message = GET_SUBJECT_PROGRESS_SUBJECT_ID_NOT_NULL)
        Long subjectId;

        public Param(UUID assessmentId, Long subjectId) {
            this.assessmentId = assessmentId;
            this.subjectId = subjectId;
            this.validateSelf();
        }
    }

    record Result(UUID id, Integer answerCount, Integer questionCount) {
    }
}
