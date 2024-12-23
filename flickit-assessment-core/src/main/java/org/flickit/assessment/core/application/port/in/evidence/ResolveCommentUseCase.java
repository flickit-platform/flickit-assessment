package org.flickit.assessment.core.application.port.in.evidence;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.RESOLVE_COMMENT_EVIDENCE_ID_NOT_NULL;

public interface ResolveCommentUseCase {

    void resolveComment(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = RESOLVE_COMMENT_EVIDENCE_ID_NOT_NULL)
        UUID id;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Builder
        public Param(UUID id, UUID currentUserId) {
            this.id = id;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }
}
