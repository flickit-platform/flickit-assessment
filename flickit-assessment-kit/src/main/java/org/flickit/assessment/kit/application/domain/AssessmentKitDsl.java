package org.flickit.assessment.kit.application.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.flickit.assessment.kit.application.port.out.assessmentkitdsl.LoadJsonKitDslPort;

import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
@RequiredArgsConstructor
public class AssessmentKitDsl {

    private final Long id;
    private final String dslFile;
    private final Long assessmentKitId;
    private final LocalDateTime creationTime;
}
