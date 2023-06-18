package org.flickit.flickitassessmentcore.adapter.out.persistence.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.out.assessment.CreateAssessmentPort;
import org.flickit.flickitassessmentcore.application.port.out.assessment.LoadAssessmentPort;
import org.flickit.flickitassessmentcore.domain.Assessment;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AssessmentPersistenceJpaAdaptor implements CreateAssessmentPort, LoadAssessmentPort {

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
}
