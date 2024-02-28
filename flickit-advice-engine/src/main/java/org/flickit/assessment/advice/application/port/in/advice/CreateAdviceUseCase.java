package org.flickit.assessment.advice.application.port.in.advice;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.advice.application.domain.AttributeLevelTarget;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.advice.common.ErrorMessageKey.*;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;

public interface CreateAdviceUseCase {

    Result createAdvice(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = CREATE_ADVICE_ASSESSMENT_ID_NOT_NULL)
        UUID assessmentId;

        @NotNull(message = CREATE_ADVICE_ATTRIBUTE_LEVEL_TARGETS_NOT_NULL)
        @Size(min = 1, message = CREATE_ADVICE_ATTRIBUTE_LEVEL_TARGETS_SIZE_MIN)
        List<AttributeLevelTarget> attributeLevelTargets;

        @NotNull(message = CREATE_ADVICE_ADVICE_QUESTIONS_NOT_NULL)
        @Size(min = 1, message = CREATE_ADVICE_ADVICE_QUESTIONS_SIZE_MIN)
        List<AdviceQuestion> adviceQuestions;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        public Param(UUID assessmentId,
                     List<AttributeLevelTarget> attributeLevelTargets,
                     List<AdviceQuestion> adviceQuestion,
                     UUID currentUserId) {
            this.assessmentId = assessmentId;
            this.attributeLevelTargets = attributeLevelTargets;
            this.adviceQuestions = adviceQuestion;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }

    record AdviceQuestion(Long questionId, Integer recommendedOptionIndex, Double benefit) {
    }

    record Result(UUID id){
    }
}
