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

public interface GetAssessmentAttributeMeasuresUseCase {

    Result getAssessmentAttributeMeasures(Param param);

    @Value
    @EqualsAndHashCode(callSuper = true)
    class Param extends SelfValidating<Param> {

        @NotNull(message = GET_ASSESSMENT_ATTRIBUTE_MEASURES_ASSESSMENT_ID_NOT_NULL)
        UUID assessmentId;

        @NotNull(message = GET_ASSESSMENT_ATTRIBUTE_MEASURES_ATTRIBUTE_ID_NOT_NULL)
        Long attributeId;

        @EnumValue(enumClass = Sort.class, message = GET_ASSESSMENT_ATTRIBUTE_MEASURES_SORT_INVALID)
        String sort;

        @EnumValue(enumClass = Order.class, message = GET_ASSESSMENT_ATTRIBUTE_MEASURES_INVALID)
        String order;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Builder
        public Param(UUID assessmentId, Long attributeId, String sort, String order, UUID currentUserId) {
            this.assessmentId = assessmentId;
            this.attributeId = attributeId;
            this.sort = sort != null && !sort.isBlank() ? sort.strip().toUpperCase() : Sort.DEFAULT.name();
            this.order = order != null && !order.isBlank() ? order.strip().toUpperCase() : Order.ASC.name();
            this.currentUserId = currentUserId;
            this.validateSelf();
        }

        @Getter
        @RequiredArgsConstructor
        @JsonFormat(shape = JsonFormat.Shape.OBJECT)
        public enum Sort {
            IMPACT_PERCENTAGE,
            GAINED_SCORE,
            MISSED_SCORE;

            public static final Sort DEFAULT = IMPACT_PERCENTAGE;
        }
    }

    record Result(List<Measure> measures) {
        public record Measure(String title,
                              Double impactPercentage,
                              Double maxPossibleScore,
                              Double gainedScore,
                              Double missedScore,
                              Double gainedScorePercentage,
                              Double missedScorePercentage) {
        }
    }
}
