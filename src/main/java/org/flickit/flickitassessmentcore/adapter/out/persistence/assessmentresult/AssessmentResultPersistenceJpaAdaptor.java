package org.flickit.flickitassessmentcore.adapter.out.persistence.assessmentresult;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.adapter.out.persistence.assessment.AssessmentJpaEntity;
import org.flickit.flickitassessmentcore.adapter.out.persistence.assessment.AssessmentJpaRepository;
import org.flickit.flickitassessmentcore.application.domain.AssessmentResult;
import org.flickit.flickitassessmentcore.application.port.out.assessmentresult.CreateAssessmentResultPort;
import org.flickit.flickitassessmentcore.application.port.out.assessmentresult.InvalidateAssessmentResultPort;
import org.flickit.flickitassessmentcore.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.flickitassessmentcore.application.service.exception.ResourceNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

import static org.flickit.flickitassessmentcore.application.service.constant.AssessmentConstants.NOT_DELETED_DELETION_TIME;
import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.CREATE_ASSESSMENT_RESULT_ASSESSMENT_ID_NOT_FOUND;


@Component
@RequiredArgsConstructor
public class AssessmentResultPersistenceJpaAdaptor implements
    InvalidateAssessmentResultPort,
    CreateAssessmentResultPort,
    LoadAssessmentResultPort {

    private final AssessmentResultJpaRepository repo;
    private final AssessmentJpaRepository assessmentRepo;

    @Override
    public void invalidateById(UUID assessmentResultId) {
        repo.invalidateById(assessmentResultId);
    }

    @Override
    public UUID persist(Param param) {
        AssessmentResultJpaEntity entity = AssessmentResultMapper.mapToJpaEntity(param);
        AssessmentJpaEntity assessment = assessmentRepo.findByIdAndDeletionTime(param.assessmentId(), NOT_DELETED_DELETION_TIME)
            .orElseThrow(() -> new ResourceNotFoundException(CREATE_ASSESSMENT_RESULT_ASSESSMENT_ID_NOT_FOUND));
        entity.setAssessment(assessment);
        AssessmentResultJpaEntity savedEntity = repo.save(entity);
        return savedEntity.getId();
    }

    @Override
    public Optional<AssessmentResult> loadByAssessmentId(UUID assessmentId) {
        var entity = repo.findFirstByAssessment_IdAndAssessment_DeletionTimeOrderByLastModificationTimeDesc(assessmentId, NOT_DELETED_DELETION_TIME);
        return entity.map(AssessmentResultMapper::mapToDomainModel);
    }

}

