package org.flickit.flickitassessmentcore.application.port.in.assessment;

import jakarta.validation.constraints.*;
import lombok.Value;
import org.flickit.flickitassessmentcore.common.SelfValidating;

import java.util.UUID;

import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.*;

public interface UpdateAssessmentUseCase {

    Result updateAssessment(Param param);

    @Value
    class Param extends SelfValidating<Param> {

        @NotNull(message = UPDATE_ASSESSMENT_ID_NOT_NULL)
        UUID id;

        @NotBlank(message = UPDATE_ASSESSMENT_TITLE_NOT_BLANK)
        @Size(min = 3, message = UPDATE_ASSESSMENT_TITLE_SIZE_MIN)
        @Size(max = 100, message = UPDATE_ASSESSMENT_TITLE_SIZE_MAX)
        String title;

        @NotNull(message = UPDATE_ASSESSMENT_ASSESSMENT_KIT_ID_NOT_NULL)
        Long assessmentKitId;

        @NotNull(message = UPDATE_ASSESSMENT_COLOR_ID_NOT_NULL)
        Integer colorId;

        public Param(UUID id, String title, Long assessmentKitId, Integer colorId) {
            this.id = id;
            this.title = title;
            this.assessmentKitId = assessmentKitId;
            this.colorId = colorId;
            this.validateSelf();
        }
    }

    record Result(UUID id) {
    }
}
