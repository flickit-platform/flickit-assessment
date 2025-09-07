package org.flickit.assessment.core.application.port.in.advicenarration;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.common.application.domain.advice.AdvicePlanItem;
import org.flickit.assessment.common.application.domain.advice.AttributeLevelTarget;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;

public interface CreateAiAdviceNarrationUseCase {

    Result createAiAdviceNarration(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = CREATE_AI_ADVICE_NARRATION_ASSESSMENT_ID_NOT_NULL)
        UUID assessmentId;

        @NotNull(message = CREATE_AI_ADVICE_NARRATION_ADVICE_LIST_ITEMS_NOT_NULL)
        List<AdvicePlanItem> adviceListItems;

        @NotNull(message = CREATE_AI_ADVICE_NARRATION_ATTRIBUTE_LEVEL_TARGETS_NOT_NULL)
        List<AttributeLevelTarget> attributeLevelTargets;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Builder
        public Param(UUID assessmentId, List<AdvicePlanItem> adviceListItems, List<AttributeLevelTarget> attributeLevelTargets, UUID currentUserId) {
            this.assessmentId = assessmentId;
            this.adviceListItems = adviceListItems;
            this.attributeLevelTargets = attributeLevelTargets;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }

    record Result(String content){
    }
}
