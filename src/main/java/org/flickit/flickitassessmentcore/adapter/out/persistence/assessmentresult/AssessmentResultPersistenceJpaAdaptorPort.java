package org.flickit.flickitassessmentcore.adapter.out.persistence.assessmentresult;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.adapter.out.persistence.assessment.AssessmentJpaEntity;
import org.flickit.flickitassessmentcore.adapter.out.persistence.assessment.AssessmentJpaRepository;
import org.flickit.flickitassessmentcore.application.domain.AssessmentResult;
import org.flickit.flickitassessmentcore.application.port.out.assessmentresult.CreateAssessmentResultPort;
import org.flickit.flickitassessmentcore.application.port.out.assessmentresult.InvalidateAssessmentResultPort;
import org.flickit.flickitassessmentcore.application.port.out.assessmentresult.LoadAssessmentResultBySubjectValueIdPort;
import org.flickit.flickitassessmentcore.application.service.exception.ResourceNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.CREATE_ASSESSMENT_RESULT_ASSESSMENT_ID_NOT_FOUND;


@Component
@RequiredArgsConstructor
public class AssessmentResultPersistenceJpaAdaptorPort implements
    InvalidateAssessmentResultPort,
    CreateAssessmentResultPort,
    LoadAssessmentResultBySubjectValueIdPort {

    private final AssessmentResultJpaRepository repo;
    private final AssessmentJpaRepository assessmentRepo;

    @Override
    public void invalidateById(UUID id) {
        repo.invalidateById(id);
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
    public Optional<AssessmentResult> load(UUID subValueId) {
        return Optional.of(AssessmentResultMapper.mapToDomainEntity(repo.findFirstBySubjectValueId(subValueId)));
    }
}

