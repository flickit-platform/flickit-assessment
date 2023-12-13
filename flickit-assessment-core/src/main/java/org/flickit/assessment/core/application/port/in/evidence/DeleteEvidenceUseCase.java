package org.flickit.assessment.core.application.port.in.evidence;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.UUID;

import static org.flickit.assessment.core.common.ErrorMessageKey.DELETE_EVIDENCE_EVIDENCE_ID_NOT_NULL;

public interface DeleteEvidenceUseCase {

    void deleteEvidence(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = DELETE_EVIDENCE_EVIDENCE_ID_NOT_NULL)
        UUID id;

        public Param(UUID id) {
            this.id = id;
            this.validateSelf();
        }
    }

}
