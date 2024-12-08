package org.flickit.assessment.core.application.port.in.attribute;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.common.application.domain.crud.Order;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.application.domain.crud.Sort;
import org.flickit.assessment.common.validation.EnumValue;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;

public interface GetAttributeScoreDetailUseCase {

    PaginatedResponse<QuestionScore> getAttributeScoreDetail(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = GET_ATTRIBUTE_SCORE_DETAIL_ASSESSMENT_ID_NOT_NULL)
        UUID assessmentId;

        @NotNull(message = GET_ATTRIBUTE_SCORE_DETAIL_ATTRIBUTE_ID_NOT_NULL)
        Long attributeId;

        @NotNull(message = GET_ATTRIBUTE_SCORE_DETAIL_MATURITY_LEVEL_ID_NOT_NULL)
        Long maturityLevelId;

        @NotNull(message = GET_ATTRIBUTE_SCORE_DETAIL_ORDER_NOT_NULL)
        @EnumValue(enumClass = Order.class, message = GET_ATTRIBUTE_SCORE_DETAIL_ORDER_INVALID)
        String order;

        @NotNull(message = GET_ATTRIBUTE_SCORE_DETAIL_SORT_NOT_NULL)
        @EnumValue(enumClass = Sort.class, message = GET_ATTRIBUTE_SCORE_DETAIL_SORT_INVALID)
        String sort;

        @Min(value = 1, message = GET_ATTRIBUTE_SCORE_DETAIL_SIZE_MIN)
        @Max(value = 100, message = GET_ATTRIBUTE_SCORE_DETAIL_SIZE_MAX)
        int size;

        @Min(value = 0, message = GET_ATTRIBUTE_SCORE_DETAIL_PAGE_MIN)
        int page;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Builder
        public Param(UUID assessmentId, Long attributeId, Long maturityLevelId, String sort, String order, int size, int page, UUID currentUserId) {
            this.assessmentId = assessmentId;
            this.attributeId = attributeId;
            this.maturityLevelId = maturityLevelId;
            this.sort = sort != null && !sort.isBlank() ? sort.strip().toUpperCase() : Sort.DEFAULT.name();
            this.order = order != null && !order.isBlank() ? order.strip().toUpperCase() : Order.DEFAULT.name();
            this.size = size;
            this.page = page;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }

    record QuestionScore(String questionnaireTitle,
                         int questionIndex,
                         String questionTitle,
                         int questionWeight,
                         Integer answerOptionIndex,
                         String answerOptionTitle,
                         Boolean answerIsNotApplicable,
                         Double answerScore,
                         Double weightedScore) {
    }
}
