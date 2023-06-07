package org.flickit.flickitassessmentcore.adapter.out.persistence.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.out.assessment.CreateAssessmentPort;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AssessmentPersistenceJpaAdaptor implements CreateAssessmentPort {

    private final AssessmentJpaRepository repository;
    @Override
    public UUID persist(Param param) {
        AssessmentEntity unsavedEntity = AssessmentMapper.mapCreateParamToJpaEntity(param);
        AssessmentEntity entity = repository.save(unsavedEntity);
        return entity.getId();
    }
}
