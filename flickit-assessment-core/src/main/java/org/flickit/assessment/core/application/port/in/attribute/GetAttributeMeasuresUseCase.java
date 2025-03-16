package org.flickit.assessment.core.application.port.in.attribute;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.common.application.domain.crud.Order;
import org.flickit.assessment.common.validation.EnumValue;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;

public interface GetAttributeMeasuresUseCase {

    Result getAttributeMeasures(Param param);

    @Value
    @EqualsAndHashCode(callSuper = true)
    class Param extends SelfValidating<Param> {

        @NotNull(message = GET_ATTRIBUTE_MEASURES_ASSESSMENT_ID_NOT_NULL)
        UUID assessmentId;

        @NotNull(message = GET_ATTRIBUTE_MEASURES_ATTRIBUTE_ID_NOT_NULL)
        Long attributeId;

        @EnumValue(enumClass = Sort.class, message = GET_ATTRIBUTE_MEASURES_SORT_INVALID)
        String sort;

        @EnumValue(enumClass = Order.class, message = GET_ATTRIBUTE_MEASURES_ORDER_INVALID)
        String order;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Builder
        public Param(UUID assessmentId, Long attributeId, String sort, String order, UUID currentUserId) {
            this.assessmentId = assessmentId;
            this.attributeId = attributeId;
            this.sort = sort != null && !sort.isBlank() ? sort.strip().toUpperCase() : Sort.DEFAULT.name();
            this.order = order != null && !order.isBlank() ? order.strip().toUpperCase() : Order.DESC.name();
            this.currentUserId = currentUserId;
            this.validateSelf();
        }

        @Getter
        @RequiredArgsConstructor
        @JsonFormat(shape = JsonFormat.Shape.OBJECT)
        public enum Sort {
            TITLE("title"),
            IMPACT_PERCENTAGE("impactPercentage"),
            PROMISED_SCORE("promisedScore"),
            GAINED_SCORE("gainedScore"),
            MISSED_SCORE("missedScore"),
            GAINED_SCORE_PERCENTAGE("gainedScorePercentage"),
            MISSED_SCORE_PERCENTAGE("missedScorePercentage");

            private final String title;

            public static final Sort DEFAULT = TITLE;
        }
    }

    record Result(List<Measure> measures) {
        public record Measure(String title,
                       Double impactPercentage,
                       Double promisedScore,
                       Double gainedScore,
                       Double missedScore,
                       Double gainedScorePercentage,
                       Double missedScorePercentage) {
        }
    }
}
