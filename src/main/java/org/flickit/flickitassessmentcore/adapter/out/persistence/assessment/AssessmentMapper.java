package org.flickit.flickitassessmentcore.adapter.out.persistence.assessment;

import org.flickit.flickitassessmentcore.adapter.out.persistence.entity.AssessmentColorEntity;
import org.flickit.flickitassessmentcore.adapter.out.persistence.entity.AssessmentEntity;
import org.flickit.flickitassessmentcore.application.port.in.assessment.CreateAssessmentCommand;
import org.springframework.stereotype.Component;

@Component
public class AssessmentMapper {
    AssessmentEntity mapCreateCommandToJpaEntity(CreateAssessmentCommand command) {
        return new AssessmentEntity(
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
