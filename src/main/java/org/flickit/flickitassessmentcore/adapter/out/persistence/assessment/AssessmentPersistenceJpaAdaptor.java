package org.flickit.flickitassessmentcore.adapter.out.persistence.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.adapter.out.persistence.entity.AssessmentEntity;
import org.flickit.flickitassessmentcore.application.port.in.assessment.CreateAssessmentCommand;
import org.flickit.flickitassessmentcore.application.port.out.assessment.CreateAssessmentPort;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AssessmentPersistenceJpaAdaptor implements CreateAssessmentPort {

    private final AssessmentJpaRepository assessmentJpaRepository;
    @Override
    public UUID persist(CreateAssessmentCommand createAssessmentCommand) {
        AssessmentEntity unsavedEntity = AssessmentMapper.mapCreateCommandToJpaEntity(createAssessmentCommand);
        AssessmentEntity entity = assessmentJpaRepository.save(unsavedEntity);
        return entity.getId();
    }
}
