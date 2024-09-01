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
    LoadAssessmentAnalysisPort,
    CreateAssessmentAnalysisPort {

    private final AssessmentAnalysisJpaRepository repository;

    @Override
    public UUID persist(AssessmentAnalysis assessmentAnalysis) {
        var unsavedEntity = AssessmentAnalysisMapper.toJpaEntity(assessmentAnalysis);
        var savedEntity = repository.save(unsavedEntity);
        return savedEntity.getId();
    }

    @Override
    public Optional<AssessmentAnalysis> loadById(UUID assessmentAnalysisId) {
        var result = repository.findById(assessmentAnalysisId);
        return result.map(AssessmentAnalysisMapper::toDomain);
    }
}
