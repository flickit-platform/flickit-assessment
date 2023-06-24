package org.flickit.flickitassessmentcore.adapter.out.persistence.assessmentresult;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.adapter.out.persistence.assessment.AssessmentJpaEntity;
import org.flickit.flickitassessmentcore.adapter.out.persistence.assessment.AssessmentJpaRepository;
import org.flickit.flickitassessmentcore.application.port.out.assessmentresult.CreateAssessmentResultPort;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AssessmentResultPersistenceJpaAdaptor implements CreateAssessmentResultPort {

    private final AssessmentResultJpaRepository repository;
    private final AssessmentJpaRepository assessmentRepository;

    @Override
    public UUID persist(Param param) {
        AssessmentResultJpaEntity entity = AssessmentResultMapper.mapToJpaEntity(param);
        AssessmentJpaEntity assessment = assessmentRepository.findById(param.assessmentId()).get();
        entity.setAssessment(assessment);
        AssessmentResultJpaEntity savedEntity = repository.save(entity);
        return savedEntity.getId();
    }
}
