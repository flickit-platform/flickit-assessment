package org.flickit.assessment.kit.application.port.in.questionimpact;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;

public interface UpdateQuestionImpactUseCase {

    void updateQuestionImpact(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = UPDATE_QUESTION_IMPACT_KIT_VERSION_ID_NOT_NULL)
        Long kitVersionId;

        @NotNull(message = UPDATE_QUESTION_IMPACT_QUESTION_IMPACT_ID_NOT_NULL)
        Long questionImpactId;

        @NotNull(message = UPDATE_QUESTION_IMPACT_ATTRIBUTE_ID_NOT_NULL)
        Long attributeId;

        @NotNull(message = UPDATE_QUESTION_IMPACT_MATURITY_LEVEL_ID_NOT_NULL)
        Long maturityLevelId;

        @NotNull(message = UPDATE_QUESTION_IMPACT_WEIGHT_NOT_NULL)
        Integer weight;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Builder
        public Param(Long kitVersionId,
                     Long questionImpactId,
                     Long attributeId,
                     Long maturityLevelId,
                     Integer weight,
                     UUID currentUserId) {
            this.kitVersionId = kitVersionId;
            this.questionImpactId = questionImpactId;
            this.attributeId = attributeId;
            this.maturityLevelId = maturityLevelId;
            this.weight = weight;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }
}
