package org.flickit.flickitassessmentcore.domain.calculate;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class Assessment {

    private final UUID id;
    private final AssessmentKit assessmentKit;
}
