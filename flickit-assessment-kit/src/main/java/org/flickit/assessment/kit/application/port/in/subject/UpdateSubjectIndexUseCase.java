package org.flickit.assessment.kit.application.port.in.subject;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;

public interface UpdateSubjectIndexUseCase {

    void updateSubjectIndex(Param param);

    @Value
    @EqualsAndHashCode(callSuper = true)
    class Param extends SelfValidating<Param> {

        @NotNull(message = UPDATE_SUBJECT_INDEX_KIT_VERSION_ID_NOT_NULL)
        Long kitVersionId;

        @NotNull(message = UPDATE_SUBJECT_INDEX_SUBJECT_ID_NOT_NULL)
        Long subjectId;

        @NotNull(message = UPDATE_SUBJECT_INDEX_INDEX_NOT_NULL)
        Integer index;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Builder
        public Param(Long kitVersionId, Long subjectId, Integer index, UUID currentUserId) {
            this.kitVersionId = kitVersionId;
            this.subjectId = subjectId;
            this.index = index;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }
}
