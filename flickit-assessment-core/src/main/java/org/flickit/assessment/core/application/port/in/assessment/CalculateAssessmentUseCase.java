package org.flickit.assessment.core.application.port.in.assessment;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.core.application.domain.MaturityLevel;

import java.util.UUID;

import static org.flickit.assessment.core.common.ErrorMessageKey.CALCULATE_ASSESSMENT_ID_NOT_NULL;

public interface CalculateAssessmentUseCase {

    Result calculateMaturityLevel(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = CALCULATE_ASSESSMENT_ID_NOT_NULL)
        UUID assessmentId;

        public Param(UUID assessmentId) {
            this.assessmentId = assessmentId;
            this.validateSelf();
        }
    }

    record Result(MaturityLevel maturityLevel) {
    }
}
