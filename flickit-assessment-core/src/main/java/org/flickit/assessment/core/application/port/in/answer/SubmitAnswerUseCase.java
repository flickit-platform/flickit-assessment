package org.flickit.assessment.core.application.port.in.answer;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.common.application.domain.notification.HasNotificationCmd;
import org.flickit.assessment.core.application.domain.notification.SubmitAnswerNotificationCmd;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;

public interface SubmitAnswerUseCase {

    Result submitAnswer(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = SUBMIT_ANSWER_ASSESSMENT_ID_NOT_NULL)
        UUID assessmentId;

        @NotNull(message = SUBMIT_ANSWER_QUESTIONNAIRE_ID_NOT_NULL)
        Long questionnaireId;

        @NotNull(message = SUBMIT_ANSWER_QUESTION_ID_NOT_NULL)
        Long questionId;

        Long answerOptionId;

        Integer confidenceLevelId;

        Boolean isNotApplicable;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Builder
        public Param(UUID assessmentId, Long questionnaireId, Long questionId, Long answerOptionId, Integer confidenceLevelId, Boolean isNotApplicable, UUID currentUserId) {
            this.assessmentId = assessmentId;
            this.questionnaireId = questionnaireId;
            this.questionId = questionId;
            this.answerOptionId = answerOptionId;
            this.confidenceLevelId = confidenceLevelId;
            this.isNotApplicable = isNotApplicable;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }

    /**
     * Represents the result of submitting an answer for the given {@link Param#questionId question}.
     */
    sealed interface Result permits NotAffected, Submitted {

        /**
         * Gets the ID of the created or updated answer.
         *
         * @return the ID of the created or updated answer. Can be {@code null} if no change occurred.
         */
        @Nullable
        UUID id();
    }

    /**
     * Represents the case where submitting the answer has no effect.
     *
     * @param id if no answer was submitted for the given {@link Param#questionId question}, this is {@code null}.
     */
    record NotAffected(@Nullable UUID id) implements Result {

        public static final NotAffected EMPTY = new NotAffected(null);
    }

    /**
     * Represents the case where an answer is successfully submitted.
     *
     * @param id              the ID of the submitted answer, which can not be {@code null}.
     * @param notificationCmd the command that may trigger a notification
     */
    record Submitted(UUID id, SubmitAnswerNotificationCmd notificationCmd) implements Result, HasNotificationCmd {
    }

}
