package org.flickit.flickitassessmentcore.application.port.in.assessment;

import jakarta.validation.constraints.NotNull;
import lombok.Value;
import org.flickit.flickitassessmentcore.common.SelfValidating;

import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.CREATE_ASSESSMENT_SPACE_ID_NOT_NULL;

@Value
public class ViewListOfSpaceAssessmentsCommand extends SelfValidating<CreateAssessmentCommand> {

    @NotNull(message = CREATE_ASSESSMENT_SPACE_ID_NOT_NULL)
    Long spaceId;

    public ViewListOfSpaceAssessmentsCommand(Long spaceId) {
        this.spaceId = spaceId;
        this.validateSelf();
    }
}
