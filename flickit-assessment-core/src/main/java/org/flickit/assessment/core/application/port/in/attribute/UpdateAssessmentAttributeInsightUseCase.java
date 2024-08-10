package org.flickit.assessment.core.application.port.in.attribute;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;


public interface UpdateAssessmentAttributeInsightUseCase {

    void updateAttributeInsight(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = UPDATE_ASSESSMENT_ATTRIBUTE_INSIGHT_ASSESSMENT_ID_NOT_NULL)
        UUID assessmentId;

        @NotNull(message = UPDATE_ASSESSMENT_ATTRIBUTE_INSIGHT_ATTRIBUTE_ID_NOT_NULL)
        Long attributeId;

        @NotNull(message = UPDATE_ASSESSMENT_ATTRIBUTE_INSIGHT_CONTENT_NOT_NULL)
        String content;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        public Param(UUID assessmentId, Long attributeId, String content, UUID currentUserId) {
            this.assessmentId = assessmentId;
            this.attributeId = attributeId;
            this.content = content;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }
}
