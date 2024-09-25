package org.flickit.assessment.kit.application.port.in.subject;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;

public interface CreateSubjectUseCase {

    long createSubject(Param param);

    @Value
    @EqualsAndHashCode(callSuper = true)
    class Param extends SelfValidating<Param> {

        private static final int DEFAULT_WEIGHT = 1;

        @NotNull(message = CREATE_SUBJECT_KIT_ID_NOT_NULL)
        Long kitId;

        @NotNull(message = CREATE_SUBJECT_INDEX_NOT_NULL)
        Integer index;

        @NotNull(message = CREATE_SUBJECT_TITLE_NOT_NULL)
        @Size(min = 3, message = CREATE_SUBJECT_TITLE_SIZE_MIN)
        @Size(max = 100, message = CREATE_SUBJECT_TITLE_SIZE_MAX)
        String title;

        @NotNull(message = CREATE_SUBJECT_DESCRIPTION_NOT_NULL)
        @Size(min = 3, message = CREATE_SUBJECT_DESCRIPTION_SIZE_MIN)
        @Size(max = 500, message = CREATE_SUBJECT_DESCRIPTION_SIZE_MAX)
        String description;

        @NotNull(message = CREATE_SUBJECT_WEIGHT_NOT_NULL)
        Integer weight;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Builder
        public Param(Long kitId, Integer index, String title, String description, Integer weight, UUID currentUserId) {
            this.kitId = kitId;
            this.index = index;
            this.title = title != null && !title.isBlank() ? title.trim() : null;
            this.description = description != null && !description.isBlank() ? description.trim() : null;
            this.weight = weight != null ? weight : DEFAULT_WEIGHT;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }
}

