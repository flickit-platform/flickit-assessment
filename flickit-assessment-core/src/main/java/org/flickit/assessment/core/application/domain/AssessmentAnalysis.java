package org.flickit.assessment.core.application.domain;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class AssessmentAnalysis {

    private final UUID id;

    private final UUID assessmentResultId;

    private final Integer type;

    private final String aiAnalysis;

    private final String assessorAnalysis;

    private final LocalDateTime aiAnalysisTime;

    private final LocalDateTime assessorAnalysisTime;

    private final String inputPath;
}
