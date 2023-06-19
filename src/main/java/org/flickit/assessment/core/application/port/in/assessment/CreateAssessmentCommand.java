package org.flickit.assessment.core.application.port.in.assessment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Value;
import org.flickit.assessment.core.common.ErrorMessageKey;
import org.flickit.assessment.core.common.SelfValidating;

@Value
public class CreateAssessmentCommand extends SelfValidating<CreateAssessmentCommand> {

    @NotBlank(message = ErrorMessageKey.CREATE_ASSESSMENT_TITLE_NOT_BLANK)
    @Size(min = 3, message = ErrorMessageKey.CREATE_ASSESSMENT_TITLE_SIZE_MIN)
    @Size(max = 100, message = ErrorMessageKey.CREATE_ASSESSMENT_TITLE_SIZE_MAX)
    String title;

    @NotNull(message = ErrorMessageKey.CREATE_ASSESSMENT_ASSESSMENT_KIT_ID_NOT_NULL)
    Long assessmentKitId;

    Integer colorId;

    @NotNull(message = ErrorMessageKey.CREATE_ASSESSMENT_SPACE_ID_NOT_NULL)
    Long spaceId;

    public CreateAssessmentCommand(Long spaceId, String title, Long assessmentKitId, Integer colorId) {
        this.title = title != null ? title.strip() : null;
        this.spaceId = spaceId;
        this.assessmentKitId = assessmentKitId;
        this.colorId = colorId;
        this.validateSelf();
    }
}
