package org.flickit.flickitassessmentcore.adapter.in.web.AssessmentProject;

import org.flickit.flickitassessmentcore.application.port.in.AssessmentProject.CreateAssessmentProjectCommand;
import org.springframework.stereotype.Component;

@Component
public class CreateAssessmentProjectWebModelMapper {
    public CreateAssessmentProjectCommand mapWebModelToCommand(CreateAssessmentProjectWebModel webModel) {
        return new CreateAssessmentProjectCommand(
            webModel.title(),
            webModel.description(),
            webModel.spaceId(),
            webModel.assessmentKitId(),
            webModel.colorId()
        );
    }
}
