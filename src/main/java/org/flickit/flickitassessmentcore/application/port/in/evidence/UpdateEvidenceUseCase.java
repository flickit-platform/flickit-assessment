package org.flickit.flickitassessmentcore.application.port.in.evidence;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.flickitassessmentcore.application.service.exception.ResourceNotFoundException;
import org.flickit.flickitassessmentcore.common.SelfValidating;

import java.util.UUID;

import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.*;

public interface UpdateEvidenceUseCase {

    /**
     * @throws ResourceNotFoundException if no assessment found for the given id
     */
    Result updateEvidence(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = UPDATE_EVIDENCE_ID_NOT_NULL)
        UUID id;

        @NotBlank(message = UPDATE_EVIDENCE_DESC_NOT_BLANK)
        @Size(min = 3, message = UPDATE_EVIDENCE_DESC_MIN_SIZE)
        @Size(max = 1000, message = UPDATE_EVIDENCE_DESC_MAX_SIZE)
        String description;

        public Param(UUID id, String description) {
            this.id = id;
            this.description = description;
            this.validateSelf();
        }
    }

    record Result(UUID id) {
    }
}
