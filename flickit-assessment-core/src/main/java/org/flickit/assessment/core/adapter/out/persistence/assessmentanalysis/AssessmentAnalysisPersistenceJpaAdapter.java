package org.flickit.assessment.core.adapter.out.persistence.assessmentanalysis;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.domain.AssessmentAnalysis;
import org.flickit.assessment.core.application.port.out.assessmentanalysis.CreateAssessmentAnalysisPort;
import org.flickit.assessment.core.application.port.out.assessmentanalysis.LoadAssessmentAnalysisPort;
import org.flickit.assessment.core.application.port.out.assessmentanalysis.UpdateAssessmentAnalysisInputPathPort;
import org.flickit.assessment.data.jpa.core.assessmentanalysis.AssessmentAnalysisJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AssessmentAnalysisPersistenceJpaAdapter implements
    LoadAssessmentAnalysisPort,
    CreateAssessmentAnalysisPort,
    UpdateAssessmentAnalysisInputPathPort {

    private final AssessmentAnalysisJpaRepository repository;

    @Override
    public Optional<AssessmentAnalysis> load(UUID assessmentResultId, Integer analysisType) {
        return repository.findByAssessmentResultIdAndType(assessmentResultId, analysisType)
            .map(AssessmentAnalysisMapper::mapToDomain);
    }

    @Override
    public UUID persist(CreateAssessmentAnalysisPort.Param param) {
        var entity = AssessmentAnalysisMapper.toJpaEntity(param);
        repository.save(entity);
        return entity.getId();
    }

    @Override
    public void updateInputPath(UUID id, String inputPath) {
        repository.updateInputPath(id, inputPath);
    }
}
