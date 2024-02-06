package org.flickit.assessment.advice.application.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class AssessmentResult {

    private final UUID id;
    private final boolean isCalculateValid;
    private final boolean isConfidenceValid;
}
