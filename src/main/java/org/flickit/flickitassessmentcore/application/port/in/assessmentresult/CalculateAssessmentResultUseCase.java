package org.flickit.flickitassessmentcore.application.port.in.assessmentresult;

import jakarta.validation.constraints.NotNull;
import lombok.Value;
import org.flickit.flickitassessmentcore.common.SelfValidating;
import org.flickit.flickitassessmentcore.domain.calculate.MaturityLevel;

import java.util.UUID;

import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.CALCULATE_MATURITY_LEVEL_ASSESSMENT_ID_NOT_NULL;

public interface CalculateAssessmentResultUseCase {

    Result calculateMaturityLevel(Param param);

    @Value
    class Param extends SelfValidating<Param> {

        @NotNull(message = CALCULATE_MATURITY_LEVEL_ASSESSMENT_ID_NOT_NULL)
        UUID assessmentId;

        public Param(UUID assessmentId) {
            this.assessmentId = assessmentId;
            this.validateSelf();
        }
    }

    record Result(MaturityLevel maturityLevel) {
    }
}
