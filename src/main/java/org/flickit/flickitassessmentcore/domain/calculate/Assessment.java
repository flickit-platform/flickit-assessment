package org.flickit.flickitassessmentcore.domain.calculate;

import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class Assessment {

    UUID id;
    AssessmentKit assessmentKit;
}
