package org.flickit.assessment.core.adapter.out.persistence.assessmentanalysis;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.port.out.assessmentanalysisinsight.LoadAssessmentAnalysisInsightPort;
import org.flickit.assessment.data.jpa.core.assessmentanalysis.AssessmentAnalysisJpaEntity;
import org.flickit.assessment.data.jpa.core.assessmentanalysis.AssessmentAnalysisJpaRepository;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaEntity;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static org.flickit.assessment.core.common.ErrorMessageKey.GET_ASSESSMENT_ANALYSIS_INSIGHT_ASSESSMENT_ANALYSIS_NOT_FOUND;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_ASSESSMENT_ANALYSIS_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class AssessmentAnalysisPersistenceJpaAdapter implements LoadAssessmentAnalysisInsightPort {

    private final AssessmentAnalysisJpaRepository repository;
    private final AssessmentResultJpaRepository assessmentResultRepository;

    @Override
    public String loadAssessmentAnalysisAiInsight(UUID assessmentId, int type) {
        AssessmentResultJpaEntity assessmentResultEntity = assessmentResultRepository.findFirstByAssessment_IdOrderByLastModificationTimeDesc(assessmentId)
            .orElseThrow(() -> new ResourceNotFoundException(GET_ASSESSMENT_ANALYSIS_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND));

        AssessmentAnalysisJpaEntity assessmentAnalysisJpaEntity = repository.findByAssessmentResultIdAndType(assessmentResultEntity.getId(), type)
            .orElseThrow(() -> new ResourceNotFoundException(GET_ASSESSMENT_ANALYSIS_INSIGHT_ASSESSMENT_ANALYSIS_NOT_FOUND));

        return assessmentAnalysisJpaEntity.getAiAnalysis();
    }
}
