package org.flickit.assessment.core.application.port.in.evidence;

import jakarta.validation.constraints.*;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;

public interface UpdateEvidenceUseCase {

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

        @Min(value = 1, message = UPDATE_EVIDENCE_EVIDENCE_TYPE_ID_MIN)
        @Max(value = 2, message = UPDATE_EVIDENCE_EVIDENCE_TYPE_ID_MAX)
        Integer evidenceTypeId;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID lastModifiedById;

        public Param(UUID id,
                     String description,
                     Integer evidenceTypeId,
                     UUID lastModifiedById) {
            this.id = id;
            this.description = description;
            this.evidenceTypeId = evidenceTypeId;
            this.lastModifiedById = lastModifiedById;
            this.validateSelf();
        }
    }

    record Result(UUID id) {
    }
}
