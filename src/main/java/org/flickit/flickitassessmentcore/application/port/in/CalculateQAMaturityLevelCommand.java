package org.flickit.flickitassessmentcore.application.port.in;

import lombok.NonNull;
import lombok.Value;
import org.flickit.flickitassessmentcore.common.SelfValidating;

import java.util.UUID;

@Value
public class CalculateQAMaturityLevelCommand extends SelfValidating<CalculateQAMaturityLevelCommand> {

    @NonNull
    Long qaId;
    @NonNull
    UUID resultId;

    public CalculateQAMaturityLevelCommand(Long qaId, UUID resultId) {
        this.qaId = qaId;
        this.resultId = resultId;
    }
}
