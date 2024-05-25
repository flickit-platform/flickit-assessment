package org.flickit.assessment.core.application.port.in.assessment;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.common.exception.ResourceNotFoundException;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.DELETE_ASSESSMENT_ID_NOT_NULL;

public interface DeleteAssessmentUseCase {

    /**
     * @throws ResourceNotFoundException if no assessment found by the given id
     */
    void deleteAssessment(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = DELETE_ASSESSMENT_ID_NOT_NULL)
        UUID id;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        public Param(UUID id, UUID currentUserId) {
            this.id = id;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }
}
