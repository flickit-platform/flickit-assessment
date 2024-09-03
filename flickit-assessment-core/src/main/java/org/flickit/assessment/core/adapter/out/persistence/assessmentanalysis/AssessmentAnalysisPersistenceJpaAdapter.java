package org.flickit.assessment.core.adapter.out.persistence.assessmentanalysis;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.domain.AssessmentAnalysis;
import org.flickit.assessment.core.application.port.out.assessmentanalysis.CreateAssessmentAnalysisPort;
import org.flickit.assessment.core.application.port.out.assessmentanalysis.LoadAssessmentAnalysisPort;
import org.flickit.assessment.data.jpa.core.assessmentanalysis.AssessmentAnalysisJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AssessmentAnalysisPersistenceJpaAdapter implements
    CreateAssessmentAnalysisPort,
    LoadAssessmentAnalysisPort {

    private final AssessmentAnalysisJpaRepository repository;

    @Override
    public void create(AssessmentAnalysis assessmentAnalysis) {
        repository.update(assessmentAnalysis.getId(), assessmentAnalysis.getAiAnalysis(), assessmentAnalysis.getAiAnalysisTime());
    }

    @Override
    public Optional<AssessmentAnalysis> loadAssessmentAnalysis(UUID assessmentResultId, int type) {
        return repository.findByAssessmentResultIdAndType(assessmentResultId, type)
            .map(AssessmentAnalysisMapper::toDomain);
    }
}
