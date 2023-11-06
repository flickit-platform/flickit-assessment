package org.flickit.assessment.core.application.port.in.confidencelevel;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.core.common.SelfValidating;

import java.util.UUID;

import static org.flickit.assessment.core.common.ErrorMessageKey.CALCULATE_CONFIDENCE_LEVEL_ASSESSMENT_ID_NOT_NULL;

public interface CalculateConfidenceLevelUseCase {

    ConfidenceLevelResult calculate(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = CALCULATE_CONFIDENCE_LEVEL_ASSESSMENT_ID_NOT_NULL)
        UUID assessmentId;

        public Param(UUID assessmentId) {
            this.assessmentId = assessmentId;
            this.validateSelf();
        }
    }

    record ConfidenceLevelResult(double value) {}
}
