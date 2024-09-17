package org.flickit.assessment.core.application.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class AssessmentAnalysis {

    private final UUID id;
    private final UUID assessmentResultId;
    private final AnalysisType type;
    private String aiAnalysis;
    private String assessorAnalysis;
    private LocalDateTime aiAnalysisTime;
    private LocalDateTime assessorAnalysisTime;
    private final String inputPath;
}
