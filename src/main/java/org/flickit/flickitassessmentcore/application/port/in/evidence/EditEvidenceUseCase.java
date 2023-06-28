package org.flickit.flickitassessmentcore.application.port.in.evidence;

import jakarta.validation.constraints.NotNull;
import lombok.Value;
import org.flickit.flickitassessmentcore.common.SelfValidating;

import java.util.UUID;

import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.EDIT_EVIDENCE_ID_NOT_NULL;

public interface EditEvidenceUseCase {

    Result editEvidence(Param param);

    @Value
    class Param extends SelfValidating<Param> {

        @NotNull(message = EDIT_EVIDENCE_ID_NOT_NULL)
        UUID id;

        //        @NotBlank(message = EDIT_EVIDENCE_DESC_NOT_BLANK)
//        @Size(min = 3, message = EDIT_EVIDENCE_DESC_SIZE_MIN)
//        @Size(max = 100, message = EDIT_EVIDENCE_DESC_SIZE_MAX)
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
