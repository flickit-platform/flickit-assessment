package org.flickit.flickitassessmentcore.application.port.in.assessment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Value;
import org.flickit.flickitassessmentcore.common.SelfValidating;

@Value
public class CreateAssessmentCommand extends SelfValidating<CreateAssessmentCommand> {

    @NotBlank
    @Size(min = 3, max = 100)
    String title;
    @NotBlank
    @Size(min = 3, max = 500)
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

        this.title = title.strip();
        this.description = description.strip();
        this.spaceId = spaceId;
        this.assessmentKitId = assessmentKitId;
        this.colorId = colorId;
        this.validateSelf();
    }
}
