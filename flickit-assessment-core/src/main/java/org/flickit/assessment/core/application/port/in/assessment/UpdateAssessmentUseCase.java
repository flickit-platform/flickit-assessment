package org.flickit.assessment.core.application.port.in.assessment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.common.exception.ResourceNotFoundException;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;

public interface UpdateAssessmentUseCase {

    /**
     * @throws ResourceNotFoundException if no assessment found by the given id
     */
    Result updateAssessment(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = UPDATE_ASSESSMENT_ID_NOT_NULL)
        UUID id;

        @NotBlank(message = UPDATE_ASSESSMENT_TITLE_NOT_BLANK)
        @Size(min = 3, message = UPDATE_ASSESSMENT_TITLE_SIZE_MIN)
        @Size(max = 100, message = UPDATE_ASSESSMENT_TITLE_SIZE_MAX)
        String title;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        public Param(UUID id, String title, UUID currentUserId) {
            this.id = id;
            this.title = title;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }

    record Result(UUID id) {
    }
}
