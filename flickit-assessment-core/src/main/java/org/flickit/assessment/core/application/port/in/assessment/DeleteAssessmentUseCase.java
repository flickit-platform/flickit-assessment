package org.flickit.assessment.core.application.port.in.assessment;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.common.exception.ResourceNotFoundException;

import java.util.UUID;

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

        public Param(UUID id) {
            this.id = id;
            this.validateSelf();
        }
    }
}
