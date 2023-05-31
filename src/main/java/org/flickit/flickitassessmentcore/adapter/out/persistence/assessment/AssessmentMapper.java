package org.flickit.flickitassessmentcore.adapter.out.persistence.assessment;

import org.flickit.flickitassessmentcore.adapter.out.persistence.entity.AssessmentColorEntity;
import org.flickit.flickitassessmentcore.adapter.out.persistence.entity.AssessmentEntity;
import org.flickit.flickitassessmentcore.application.port.in.assessment.CreateAssessmentCommand;

public class AssessmentMapper {
    static AssessmentEntity mapCreateCommandToJpaEntity(CreateAssessmentCommand command) {
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
