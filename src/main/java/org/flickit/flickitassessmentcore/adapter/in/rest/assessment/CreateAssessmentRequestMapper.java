package org.flickit.flickitassessmentcore.adapter.in.rest.assessment;

import org.flickit.flickitassessmentcore.application.port.in.assessment.CreateAssessmentCommand;

public class CreateAssessmentRequestMapper {
    public static CreateAssessmentCommand mapWebModelToCommand(CreateAssessmentRequestDto webModel, Long spaceId) {
        return new CreateAssessmentCommand(
            webModel.title(),
            webModel.description(),
            spaceId,
            webModel.assessmentKitId(),
            webModel.colorId()
        );
    }
}
