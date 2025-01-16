package org.flickit.assessment.core.application.port.in.questions;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.UUID;

import static org.flickit.assessment.core.common.ErrorMessageKey.*;

public interface GetQuestionIssuesUseCase {

    Result getQuestionIssues(Param param);

    @Value
    @EqualsAndHashCode(callSuper = true)
    class Param extends SelfValidating<Param> {

        @NotNull(message = GET_QUESTION_ISSUES_QUESTION_ID_NOT_NULL)
        Long questionId;

        @NotNull(message = GET_QUESTION_ISSUES_ASSESSMENT_ID_NOT_NULL)
        UUID assessmentId;

        @NotNull(message = GET_QUESTION_ISSUES_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Builder
        public Param(Long questionId, UUID assessmentId, UUID currentUserId) {
            this.questionId = questionId;
            this.assessmentId = assessmentId;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }

    record Result(
        boolean isUnanswered,
        boolean isAnsweredWithLowConfidence,
        boolean isAnsweredWithoutEvidences,
        int unresolvedCommentsCount) {
    }
}
