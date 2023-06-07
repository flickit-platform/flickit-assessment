package org.flickit.flickitassessmentcore.application.port.in.assessment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Value;
import org.flickit.flickitassessmentcore.common.SelfValidating;

@Value
public class CreateAssessmentCommand extends SelfValidating<CreateAssessmentCommand> {

    @NotBlank
    String title;
    @NotBlank
    String description;
    @NotNull
    Long assessmentKitId;
    long colorId;
    @NotNull
    Long spaceId;

    public CreateAssessmentCommand(String title,
                                   String description,
                                   Long spaceId,
                                   Long assessmentKitId,
                                   Long colorId) {

        this.title = title;
        this.description = description;
        this.spaceId = spaceId;
        this.assessmentKitId = assessmentKitId;
        this.colorId = colorId;
        this.validateSelf();
    }
}
