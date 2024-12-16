package org.flickit.assessment.advice.application.port.in.adviceitem;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.advice.application.domain.adviceitem.CostLevel;
import org.flickit.assessment.advice.application.domain.adviceitem.ImpactLevel;
import org.flickit.assessment.advice.application.domain.adviceitem.PriorityLevel;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.common.validation.EnumValue;

import java.util.UUID;

import static org.flickit.assessment.advice.common.ErrorMessageKey.*;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;

public interface UpdateAdviceItemUseCase {

    void updateAdviceItem(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = UPDATE_ADVICE_ITEM_ID_NOT_NULL)
        UUID adviceItemId;

        @NotNull(message = UPDATE_ADVICE_ITEM_TITLE_NOT_NULL)
        @Size(min = 3, message = UPDATE_ADVICE_ITEM_TITLE_SIZE_MIN)
        @Size(max = 100, message = UPDATE_ADVICE_ITEM_TITLE_SIZE_MAX)
        String title;

        @NotNull(message = UPDATE_ADVICE_ITEM_DESCRIPTION_NOT_NULL)
        @Size(min = 3, message = UPDATE_ADVICE_ITEM_DESCRIPTION_SIZE_MIN)
        @Size(max = 3000, message = UPDATE_ADVICE_ITEM_DESCRIPTION_SIZE_MAX)
        String description;

        @NotNull(message = UPDATE_ADVICE_ITEM_COST_NOT_NULL)
        @EnumValue(enumClass = CostLevel.class, message = UPDATE_ADVICE_ITEM_COST_INVALID)
        String cost;

        @NotNull(message = UPDATE_ADVICE_ITEM_PRIORITY_NOT_NULL)
        @EnumValue(enumClass = PriorityLevel.class, message = UPDATE_ADVICE_ITEM_PRIORITY_INVALID)
        String priority;

        @NotNull(message = UPDATE_ADVICE_ITEM_IMPACT_NOT_NULL)
        @EnumValue(enumClass = ImpactLevel.class, message = UPDATE_ADVICE_ITEM_IMPACT_INVALID)
        String impact;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Builder
        public Param(UUID adviceItemId, String title, String description, String cost, String priority, String impact, UUID currentUserId) {
            this.adviceItemId = adviceItemId;
            this.title = title != null && !title.isBlank() ? title.strip() : null;
            this.description = description != null && !description.isBlank() ? description.strip() : null;
            this.cost = cost;
            this.priority = priority;
            this.impact = impact;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }
}
