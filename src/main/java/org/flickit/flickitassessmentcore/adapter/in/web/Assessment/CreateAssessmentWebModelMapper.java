package org.flickit.flickitassessmentcore.adapter.in.web.Assessment;

import org.flickit.flickitassessmentcore.application.port.in.Assessment.CreateAssessmentCommand;
import org.springframework.stereotype.Component;

@Component
public class CreateAssessmentWebModelMapper {
    public CreateAssessmentCommand mapWebModelToCommand(CreateAssessmentWebModel webModel) {
        return new CreateAssessmentCommand(
            webModel.title(),
            webModel.description(),
            webModel.spaceId(),
            webModel.assessmentKitId(),
            webModel.colorId()
        );
    }
}
