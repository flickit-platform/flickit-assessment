package org.flickit.assessment.kit.application.port.in.questionimpact;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;


public interface CreateQuestionImpactUseCase {

    long createQuestionImpact(Param param);

    @Value
    @EqualsAndHashCode(callSuper = true)
    class Param extends SelfValidating<Param> {

        @NotNull(message = CREATE_QUESTION_IMPACT_KIT_VERSION_ID_NOT_NULL)
        Long kitVersionId;

        @NotNull(message = CREATE_QUESTION_IMPACT_ATTRIBUTE_ID_NOT_NULL)
        Long attributeId;

        @NotNull(message = CREATE_QUESTION_IMPACT_MATURITY_LEVEL_ID_NOT_NULL)
        Long maturityLevelId;

        @NotNull(message = CREATE_QUESTION_IMPACT_WEIGHT_NOT_NULL)
        Integer weight;

        @NotNull(message = CREATE_QUESTION_IMPACT_QUESTION_ID_NOT_NULL)
        Long questionId;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Builder
        public Param(Long kitVersionId, Long attributeId, Long maturityLevelId, Integer weight, Long questionId, UUID currentUserId) {
            this.kitVersionId = kitVersionId;
            this.attributeId = attributeId;
            this.maturityLevelId = maturityLevelId;
            this.weight = weight;
            this.questionId = questionId;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }
}
