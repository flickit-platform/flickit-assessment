package org.flickit.assessment.core.application.service.assessmentanalysis;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.port.in.assessmentanalysis.CreateAssessmentAnalysisUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateAssessmentAnalysisService implements CreateAssessmentAnalysisUseCase {

    @Override
    public Result createAiAnalysis(Param param) {
        return null;
    }
}
