package org.flickit.assessment.core.application.port.in.question;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.core.application.domain.ConfidenceLevel;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_ASSESSMENT_QUESTION_ASSESSMENT_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_ASSESSMENT_QUESTION_QUESTION_ID_NOT_NULL;

public interface GetAssessmentQuestionUseCase {

    Result getQuestion(Param param);

    @Value
    @EqualsAndHashCode(callSuper = true)
    class Param extends SelfValidating<Param> {

        @NotNull(message = GET_ASSESSMENT_QUESTION_ASSESSMENT_ID_NOT_NULL)
        UUID assessmentId;

        @NotNull(message = GET_ASSESSMENT_QUESTION_QUESTION_ID_NOT_NULL)
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

    record Result(Long id,
                  Integer index,
                  String title,
                  String hint,
                  Boolean mayNotBeApplicable,
                  List<Option> options,
                  QuestionAnswer answer,
                  Issues issues,
                  Counts counts) {
    }

    record Option(Long id, Integer index, String title) {
    }

    record QuestionAnswer(Option selectedOption,
                          ConfidenceLevel confidenceLevel,
                          Boolean isNotApplicable,
                          Boolean approved) {
    }

    record Issues(boolean isUnanswered,
                  boolean isAnsweredWithLowConfidence,
                  boolean isAnsweredWithoutEvidences,
                  int unresolvedCommentsCount,
                  boolean hasUnapprovedAnswer) {
    }

    record Counts(int evidences,
                  int comments,
                  int answerHistories) {
    }
}
