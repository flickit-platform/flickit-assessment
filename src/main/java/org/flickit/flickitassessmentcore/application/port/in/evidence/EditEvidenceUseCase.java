package org.flickit.flickitassessmentcore.application.port.in.evidence;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Value;
import org.flickit.flickitassessmentcore.common.SelfValidating;

import java.util.UUID;

import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.*;

public interface EditEvidenceUseCase {

    Result editEvidence(Param param);

    @Value
    class Param extends SelfValidating<Param> {

        @NotNull(message = EDIT_EVIDENCE_ID_NOT_NULL)
        UUID id;

        @NotBlank(message = EDIT_EVIDENCE_DESC_NOT_BLANK)
        @Size(min = 3, message = EDIT_EVIDENCE_DESC_MIN_SIZE)
        @Size(max = 100, message = EDIT_EVIDENCE_DESC_MAX_SIZE)
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
