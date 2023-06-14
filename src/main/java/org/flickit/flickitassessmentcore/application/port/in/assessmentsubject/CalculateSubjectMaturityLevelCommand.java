package org.flickit.flickitassessmentcore.application.port.in.assessmentsubject;

import lombok.NonNull;
import lombok.Value;
import org.flickit.flickitassessmentcore.common.SelfValidating;

@Value
public class CalculateSubjectMaturityLevelCommand extends SelfValidating<CalculateSubjectMaturityLevelCommand> {

    @NonNull
    Long subId;

    public CalculateSubjectMaturityLevelCommand(@NonNull Long subId) {
        this.subId = subId;
    }
}
