package org.flickit.flickitassessmentcore.adapter.out.persistence.assessmentresult;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.adapter.out.persistence.assessment.AssessmentJpaEntity;
import org.flickit.flickitassessmentcore.adapter.out.persistence.assessment.AssessmentJpaRepository;
import org.flickit.flickitassessmentcore.application.port.out.assessmentresult.*;
import org.flickit.flickitassessmentcore.application.service.exception.ResourceNotFoundException;
import org.flickit.flickitassessmentcore.domain.AssessmentResult;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.CALCULATE_MATURITY_LEVEL_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.CREATE_ASSESSMENT_RESULT_ASSESSMENT_ID_NOT_FOUND;


@Component
@RequiredArgsConstructor
public class AssessmentResultPersistenceJpaAdaptor implements
    InvalidateAssessmentResultPort,
    SaveAssessmentResultPort,
    LoadAssessmentResultPort,
    LoadAssessmentResultByAssessmentPort,
    CreateAssessmentResultPort {

    private final AssessmentResultJpaRepository repository;
    private final AssessmentJpaRepository assessmentRepository;

    @Override
    public void invalidateById(UUID id) {
        repository.invalidateById(id);
    }

    @Override
    public AssessmentResult saveAssessmentResult(AssessmentResult assessmentResult) {
        return AssessmentResultMapper.mapToDomainModel(repository.save(AssessmentResultMapper.mapToJpaEntity(assessmentResult)));
    }

    @Override
    public AssessmentResult loadAssessmentResult(UUID resultId) {
        return AssessmentResultMapper.mapToDomainModel(
            repository.findById(resultId).orElseThrow(
                () -> new ResourceNotFoundException(CALCULATE_MATURITY_LEVEL_ASSESSMENT_RESULT_NOT_FOUND)));
    }

    @Override
    public Result loadAssessmentResultByAssessmentId(UUID assessmentId) {
        return new Result(repository.findByAssessmentId(assessmentId).stream()
            .map(AssessmentResultMapper::mapToDomainModel)
            .toList());
    }

    @Override
    public UUID persist(Param param) {
        AssessmentResultJpaEntity entity = AssessmentResultMapper.mapToJpaEntity(param);
        AssessmentJpaEntity assessment = assessmentRepository.findById(param.assessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(CREATE_ASSESSMENT_RESULT_ASSESSMENT_ID_NOT_FOUND));
        entity.setAssessment(assessment);
        AssessmentResultJpaEntity savedEntity = repository.save(entity);
        return savedEntity.getId();
    }
}

