package org.flickit.assessment.core.application.port.in.question;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;

public interface GetQuestionIssuesUseCase {

    Result getQuestionIssues(Param param);

    @Value
    @EqualsAndHashCode(callSuper = true)
    class Param extends SelfValidating<Param> {

        @NotNull(message = GET_QUESTION_ISSUES_ASSESSMENT_ID_NOT_NULL)
        UUID assessmentId;

        @NotNull(message = GET_QUESTION_ISSUES_QUESTION_ID_NOT_NULL)
        Long questionId;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Builder
        public Param(UUID assessmentId, Long questionId, UUID currentUserId) {
            this.assessmentId = assessmentId;
            this.questionId = questionId;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }

    record Result(boolean isUnanswered,
                  boolean isAnsweredWithLowConfidence,
                  boolean isAnsweredWithoutEvidences,
                  int unresolvedCommentsCount) {
    }
}
