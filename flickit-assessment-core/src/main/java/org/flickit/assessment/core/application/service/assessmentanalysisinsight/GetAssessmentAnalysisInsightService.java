package org.flickit.assessment.core.application.service.assessmentanalysisinsight;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.domain.AssessmentAnalysisInsight;
import org.flickit.assessment.core.application.domain.AssessmentAnalysisType;
import org.flickit.assessment.core.application.port.in.assessmentanalysisinsight.GetAssessmentAnalysisInsightUseCase;
import org.flickit.assessment.core.application.port.out.assessmentanalysisinsight.LoadAssessmentAnalysisInsightPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetAssessmentAnalysisInsightService implements GetAssessmentAnalysisInsightUseCase {

    private final LoadAssessmentAnalysisInsightPort loadAssessmentAnalysisInsightPort;

    @Override
    public AssessmentAnalysisInsight getAssessmentAnalysisInsight(Param param) {
        int type = AssessmentAnalysisType.valueOf(param.getType()).ordinal();
        String aiAnalysis = loadAssessmentAnalysisInsightPort.loadAssessmentAnalysisAiInsight(param.getAssessmentId(), type);
        return null;
    }
}
