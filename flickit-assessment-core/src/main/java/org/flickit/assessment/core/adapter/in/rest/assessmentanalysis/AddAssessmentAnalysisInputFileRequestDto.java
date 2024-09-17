package org.flickit.assessment.core.adapter.in.rest.assessmentanalysis;

import org.springframework.web.multipart.MultipartFile;

public record AddAssessmentAnalysisInputFileRequestDto(MultipartFile inputFile, Integer analysisType) {
}
