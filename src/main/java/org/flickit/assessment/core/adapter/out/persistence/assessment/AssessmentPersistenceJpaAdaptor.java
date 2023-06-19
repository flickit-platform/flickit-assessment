package org.flickit.assessment.core.adapter.out.persistence.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.port.out.assessment.CreateAssessmentPort;
import org.flickit.assessment.core.application.port.out.assessment.LoadAssessmentPort;
import org.flickit.assessment.core.application.port.out.assessment.SaveAssessmentPort;
import org.flickit.assessment.core.domain.Assessment;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AssessmentPersistenceJpaAdaptor implements CreateAssessmentPort, LoadAssessmentPort, SaveAssessmentPort {

    private final AssessmentJpaRepository repository;

    @Override
    public UUID persist(Param param) {
        AssessmentJpaEntity unsavedEntity = AssessmentMapper.mapCreateParamToJpaEntity(param);
        AssessmentJpaEntity entity = repository.save(unsavedEntity);
        return entity.getId();
    }

    @Override
    public Assessment loadAssessment(UUID assessmentId) {
        AssessmentJpaEntity assessmentEntity = repository.getReferenceById(assessmentId);
        return AssessmentMapper.mapToDomainModel(assessmentEntity);
    }

    @Override
    public Assessment saveAssessment(Assessment assessment) {
        return AssessmentMapper.mapToDomainModel(repository.save(AssessmentMapper.mapToJpaEntity(assessment)));
    }
}
