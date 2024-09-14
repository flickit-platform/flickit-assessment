package org.flickit.assessment.core.adapter.out.persistence.assessmentresult;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.adapter.out.persistence.kit.maturitylevel.MaturityLevelMapper;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.domain.MaturityLevel;
import org.flickit.assessment.core.application.port.out.assessmentresult.CreateAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.InvalidateAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.data.jpa.core.assessment.AssessmentJpaEntity;
import org.flickit.assessment.data.jpa.core.assessment.AssessmentJpaRepository;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaEntity;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaRepository;
import org.flickit.assessment.data.jpa.kit.maturitylevel.MaturityLevelJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

import static org.flickit.assessment.core.common.ErrorMessageKey.CREATE_ASSESSMENT_RESULT_ASSESSMENT_ID_NOT_FOUND;

@Component("coreAssessmentResultPersistenceJpaAdapter")
@RequiredArgsConstructor
public class AssessmentResultPersistenceJpaAdapter implements
    InvalidateAssessmentResultPort,
    CreateAssessmentResultPort,
    LoadAssessmentResultPort {

    private final AssessmentResultJpaRepository repo;
    private final AssessmentJpaRepository assessmentRepo;
    private final MaturityLevelJpaRepository maturityLevelRepository;

    @Override
    public void invalidateById(UUID assessmentResultId, Boolean isCalculateValid, Boolean isConfidenceValid) {
        repo.invalidateById(assessmentResultId, isCalculateValid, isConfidenceValid);
    }

    @Override
    public UUID persist(Param param) {
        AssessmentResultJpaEntity entity = AssessmentResultMapper.mapToJpaEntity(param);
        AssessmentJpaEntity assessment = assessmentRepo.findById(param.assessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(CREATE_ASSESSMENT_RESULT_ASSESSMENT_ID_NOT_FOUND));
        entity.setAssessment(assessment);
        AssessmentResultJpaEntity savedEntity = repo.save(entity);
        return savedEntity.getId();
    }

    @Override
    public Optional<AssessmentResult> loadByAssessmentId(UUID assessmentId) {
        var entity = repo.findFirstByAssessment_IdOrderByLastModificationTimeDesc(assessmentId);
        if (entity.isEmpty())
            return Optional.empty();
        MaturityLevel maturityLevel = null;
        var maturityLevelId = entity.get().getMaturityLevelId();
        if (maturityLevelId != null) {
            var maturityLevelEntity = maturityLevelRepository.findByIdAndKitVersionId(maturityLevelId, entity.get().getKitVersionId());
            maturityLevel = maturityLevelEntity.map(maturityLevelJpaEntity ->
                MaturityLevelMapper.mapToDomainModel(maturityLevelJpaEntity, null)).orElse(null);
        }
        return Optional.of(AssessmentResultMapper.mapToDomainModel(entity.get(), maturityLevel));
    }
}

