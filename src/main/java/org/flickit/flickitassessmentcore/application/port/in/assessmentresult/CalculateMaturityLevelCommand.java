package org.flickit.flickitassessmentcore.application.port.in.assessmentresult;

import jakarta.validation.constraints.NotNull;
import lombok.Value;
import org.flickit.flickitassessmentcore.common.ErrorMessageKey;
import org.flickit.flickitassessmentcore.common.SelfValidating;

import java.util.UUID;

@Value
public class CalculateMaturityLevelCommand extends SelfValidating<CalculateMaturityLevelCommand> {

    @NotNull(message = ErrorMessageKey.CALCULATE_MATURITY_LEVEL_ASSESSMENT_ID_NOT_NULL)
    UUID assessmentId;

    public CalculateMaturityLevelCommand(UUID assessmentId) {
        this.assessmentId = assessmentId;
    }
}
