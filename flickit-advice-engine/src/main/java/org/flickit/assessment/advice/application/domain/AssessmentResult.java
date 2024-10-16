package org.flickit.assessment.advice.application.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class AssessmentResult {

    private final UUID id;

    private final long kitVersionId;
}
