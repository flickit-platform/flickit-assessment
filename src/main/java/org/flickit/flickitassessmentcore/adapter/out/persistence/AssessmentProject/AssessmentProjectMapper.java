package org.flickit.flickitassessmentcore.adapter.out.persistence.AssessmentProject;

import org.flickit.flickitassessmentcore.adapter.out.persistence.entity.AssessmentColorEntity;
import org.flickit.flickitassessmentcore.adapter.out.persistence.entity.AssessmentProjectEntity;
import org.flickit.flickitassessmentcore.application.port.in.AssessmentProject.CreateAssessmentProjectCommand;
import org.springframework.stereotype.Component;

@Component
public class AssessmentProjectMapper {
    AssessmentProjectEntity mapCreateCommandToJpaEntity(CreateAssessmentProjectCommand command) {
        return new AssessmentProjectEntity(
            null,
            command.getCode(),
            command.getTitle(),
            command.getDescription(),
            command.getCreationTime(),
            command.getLastModificationDate(),
            command.getAssessmentKitId(),
            new AssessmentColorEntity(
                command.getColor().getId(),
                command.getColor().getTitle(),
                command.getColor().getColorCode()),
            command.getSpaceId(),
            null);
    }
}
