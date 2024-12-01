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


public interface CreateAdviceItemUseCase {

    Result createAdviceItem(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = CREATE_ADVICE_ITEM_ASSESSMENT_ID_NOT_NULL)
        UUID assessmentId;

        @NotNull(message = CREATE_ADVICE_ITEM_TITLE_NOT_NULL)
        @Size(min = 3, message = CREATE_ADVICE_ITEM_TITLE_SIZE_MIN)
        @Size(max = 100, message = CREATE_ADVICE_ITEM_TITLE_SIZE_MAX)
        String title;

        @NotNull(message = CREATE_ADVICE_ITEM_DESCRIPTION_NOT_NULL)
        @Size(min = 3, message = CREATE_ADVICE_ITEM_DESCRIPTION_SIZE_MIN)
        @Size(max = 3000, message = CREATE_ADVICE_ITEM_DESCRIPTION_SIZE_MAX)
        String description;

        @NotNull(message = CREATE_ADVICE_ITEM_COST_NOT_NULL)
        @EnumValue(enumClass = CostLevel.class, message = CREATE_ADVICE_ITEM_COST_INVALID)
        String cost;

        @NotNull(message = CREATE_ADVICE_ITEM_PRIORITY_NOT_NULL)
        @EnumValue(enumClass = PriorityLevel.class, message = CREATE_ADVICE_ITEM_PRIORITY_INVALID)
        String priority;

        @NotNull(message = CREATE_ADVICE_ITEM_IMPACT_NOT_NULL)
        @EnumValue(enumClass = ImpactLevel.class, message = CREATE_ADVICE_ITEM_IMPACT_INVALID)
        String impact;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Builder
        public Param(UUID assessmentId, String title, String description, String cost, String priority, String impact, UUID currentUserId) {
            this.assessmentId = assessmentId;
            this.title = title != null && !title.isBlank() ? title.strip() : null;
            this.description = description != null && !description.isBlank() ? description.strip() : null;
            this.cost = cost;
            this.priority = priority;
            this.impact = impact;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }

    record Result(UUID id) {
    }
}
