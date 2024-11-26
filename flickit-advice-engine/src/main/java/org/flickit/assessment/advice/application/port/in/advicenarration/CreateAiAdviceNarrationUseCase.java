package org.flickit.assessment.advice.application.port.in.advicenarration;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.advice.application.domain.AttributeLevelTarget;
import org.flickit.assessment.advice.application.domain.advice.AdviceListItem;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.common.application.domain.ID;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.advice.common.ErrorMessageKey.*;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;

public interface CreateAiAdviceNarrationUseCase {

    Result createAiAdviceNarration(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = CREATE_AI_ADVICE_NARRATION_ASSESSMENT_ID_NOT_NULL)
        ID assessmentId;

        @NotNull(message = CREATE_AI_ADVICE_NARRATION_ADVICE_LIST_ITEMS_NOT_NULL)
        List<AdviceListItem> adviceListItems;

        @NotNull(message = CREATE_AI_ADVICE_NARRATION_ATTRIBUTE_LEVEL_TARGETS_NOT_NULL)
        List<AttributeLevelTarget> attributeLevelTargets;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        ID currentUserId;

        public Param(ID assessmentId, List<AdviceListItem> adviceListItems, List<AttributeLevelTarget> attributeLevelTargets, ID currentUserId) {
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
