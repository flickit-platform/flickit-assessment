package org.flickit.assessment.advice.application.port.in.advice;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.advice.application.domain.AttributeLevelTarget;
import org.flickit.assessment.advice.application.domain.advice.AdviceListItem;
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

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        public Param(UUID assessmentId, List<AttributeLevelTarget> attributeLevelTargets, UUID currentUserId) {
            this.assessmentId = assessmentId;
            this.attributeLevelTargets = attributeLevelTargets;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }

    record Result(List<AdviceListItem> adviceItems) {
    }
}
