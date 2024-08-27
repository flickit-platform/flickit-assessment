package org.flickit.assessment.core.application.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class SubjectInsight {

    private final String insight;
    private final LocalDateTime insightTime;
    private final boolean isValid;
}
