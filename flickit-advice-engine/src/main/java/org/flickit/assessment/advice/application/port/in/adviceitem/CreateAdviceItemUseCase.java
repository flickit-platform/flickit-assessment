package org.flickit.assessment.advice.application.port.in.adviceitem;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.advice.application.domain.adviceitem.CostType;
import org.flickit.assessment.advice.application.domain.adviceitem.ImpactType;
import org.flickit.assessment.advice.application.domain.adviceitem.PriorityType;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.common.validation.EnumValue;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.advice.common.ErrorMessageKey.*;


public interface CreateAdviceItemUseCase {

    Result createAdviceItem(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = CREATE_ADVICE_ITEM_ASSESSMENT_ID_NOT_NULL)
        UUID assessmentId;

        @NotNull(message = CREATE_ADVICE_ITEM_TITLE_NOT_NULL)
        @Size(min = 3, message = CREATE_ADVICE_ITEM_TITLE_SIZE_MIN )
        @Size(max = 500, message = CREATE_ADVICE_ITEM_TITLE_SIZE_MAX)
        String title;

        @NotNull(message = CREATE_ADVICE_ITEM_ASSESSMENT_RESULT_ID_NOT_NULL)
        UUID assessmentResultId;

        @Size(min = 3, message = CREATE_ADVICE_ITEM_DESCRIPTION_SIZE_MIN)
        @Size(max = 1000, message = CREATE_ADVICE_ITEM_DESCRIPTION_SIZE_MAX)
        String description;

        @NotNull(message = CREATE_ADVICE_ITEM_COST_NOT_NULL)
        @EnumValue(enumClass = CostType.class, message = CREATE_ADVICE_ITEM_COST_INVALID)
        String cost;

        @NotNull(message = CREATE_ADVICE_ITEM_PRIORITY_NOT_NULL)
        @EnumValue(enumClass = PriorityType.class, message = CREATE_ADVICE_ITEM_PRIORITY_INVALID)
        String priority;

        @NotNull(message = CREATE_ADVICE_ITEM_IMPACT_NOT_NULL)
        @EnumValue(enumClass = ImpactType.class, message = CREATE_ADVICE_ITEM_IMPACT_INVALID)
        String impact;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Builder
        public Param(UUID assessmentId, String title, UUID assessmentResultId, String description, String cost, String priority, String impact, UUID currentUserId) {
            this.assessmentId = assessmentId;
            this.title = title != null && !title.isBlank() ? title.strip() : null;
            this.assessmentResultId = assessmentResultId;
            this.description = description != null ? description.strip() : null;
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
