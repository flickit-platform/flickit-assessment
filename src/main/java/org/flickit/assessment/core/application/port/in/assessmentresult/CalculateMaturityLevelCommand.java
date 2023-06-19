package org.flickit.assessment.core.application.port.in.assessmentresult;

import lombok.NonNull;
import lombok.Value;
import org.flickit.assessment.core.common.SelfValidating;

import java.util.UUID;

@Value
public class CalculateMaturityLevelCommand extends SelfValidating<CalculateMaturityLevelCommand> {

    @NonNull
    UUID assessmentId;

    public CalculateMaturityLevelCommand(UUID assessmentId) {
        this.assessmentId = assessmentId;
    }
}
