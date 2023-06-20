package org.flickit.assessment.core.application.port.in.assessmentresult;

import jakarta.validation.constraints.NotNull;
import lombok.Value;
import org.flickit.assessment.core.common.ErrorMessageKey;
import org.flickit.assessment.core.common.SelfValidating;

import java.util.UUID;

@Value
public class CalculateMaturityLevelCommand extends SelfValidating<CalculateMaturityLevelCommand> {

    @NotNull(message = ErrorMessageKey.CALCULATE_MATURITY_LEVEL_ASSESSMENT_ID_NOT_NULL)
    UUID assessmentId;

    public CalculateMaturityLevelCommand(UUID assessmentId) {
        this.assessmentId = assessmentId;
    }
}
