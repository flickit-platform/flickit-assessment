package org.flickit.flickitassessmentcore.adapter.out.persistence.AssessmentProject;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.adapter.out.persistence.entity.AssessmentProjectEntity;
import org.flickit.flickitassessmentcore.application.port.in.AssessmentProject.CreateAssessmentProjectCommand;
import org.flickit.flickitassessmentcore.application.port.out.AssessmentProject.CreateAssessmentProjectPort;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AssessmentProjectPersistence implements CreateAssessmentProjectPort {

    private final AssessmentProjectJpaRepository assessmentProjectJpaRepository;
    private final AssessmentProjectMapper mapper;
    @Override
    public UUID persist(CreateAssessmentProjectCommand createAssessmentProjectCommand) {
        AssessmentProjectEntity unsavedEntity = mapper.mapCreateCommandToJpaEntity(createAssessmentProjectCommand);
        AssessmentProjectEntity entity = assessmentProjectJpaRepository.save(unsavedEntity);
        return entity.getId();
    }
}
