package org.flickit.flickitassessmentcore.adapter.out.persistence.Assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.adapter.out.persistence.entity.AssessmentEntity;
import org.flickit.flickitassessmentcore.application.port.in.Assessment.CreateAssessmentCommand;
import org.flickit.flickitassessmentcore.application.port.out.Assessment.CreateAssessmentPort;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AssessmentPersistence implements CreateAssessmentPort {

    private final AssessmentJpaRepository assessmentJpaRepository;
    private final AssessmentMapper mapper;
    @Override
    public UUID persist(CreateAssessmentCommand createAssessmentCommand) {
        AssessmentEntity unsavedEntity = mapper.mapCreateCommandToJpaEntity(createAssessmentCommand);
        AssessmentEntity entity = assessmentJpaRepository.save(unsavedEntity);
        return entity.getId();
    }
}
