package org.flickit.flickitassessmentcore.adapter.in.rest.assessment;

import org.flickit.flickitassessmentcore.application.port.in.assessment.CreateAssessmentCommand;
import org.springframework.stereotype.Component;

@Component
public class CreateAssessmentWebModelMapper {
    public CreateAssessmentCommand mapWebModelToCommand(CreateAssessmentWebModel webModel, Long spaceId) {
        return new CreateAssessmentCommand(
            webModel.title(),
            webModel.description(),
            spaceId,
            webModel.assessmentKitId(),
            webModel.colorId()
        );
    }
}
