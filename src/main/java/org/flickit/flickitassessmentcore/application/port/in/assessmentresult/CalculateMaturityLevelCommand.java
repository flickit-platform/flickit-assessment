package org.flickit.flickitassessmentcore.application.port.in.assessmentresult;

import lombok.NonNull;
import lombok.Value;
import org.flickit.flickitassessmentcore.common.SelfValidating;

import java.util.UUID;

@Value
public class CalculateMaturityLevelCommand extends SelfValidating<CalculateMaturityLevelCommand> {

    @NonNull
    UUID assessmentId;

    public CalculateMaturityLevelCommand(UUID assessmentId) {
        this.assessmentId = assessmentId;
    }
}
