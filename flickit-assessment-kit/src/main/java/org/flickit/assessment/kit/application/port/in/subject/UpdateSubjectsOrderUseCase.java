package org.flickit.assessment.kit.application.port.in.subject;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;

public interface UpdateSubjectsOrderUseCase {

    void updateSubjectsOrder(Param param);

    @Value
    @EqualsAndHashCode(callSuper = true)
    class Param extends SelfValidating<Param> {

        @NotNull(message = UPDATE_SUBJECTS_ORDER_KIT_VERSION_ID_NOT_NULL)
        Long kitVersionId;

        @NotNull(message = UPDATE_SUBJECTS_ORDER_SUBJECTS_NOT_NULL)
        @Size(min = 1, message = UPDATE_SUBJECTS_ORDER_SUBJECTS_SIZE_MIN)
        List<SubjectParam> subjects;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Builder
        public Param(Long kitVersionId, List<SubjectParam> subjects, UUID currentUserId) {
            this.kitVersionId = kitVersionId;
            this.subjects = subjects;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }

    @Value
    @EqualsAndHashCode(callSuper = true)
    class SubjectParam extends SelfValidating<SubjectParam> {

        @NotNull(message = UPDATE_SUBJECTS_ORDER_SUBJECT_ID_NOT_NULL)
        Long id;

        @NotNull(message = UPDATE_SUBJECTS_ORDER_INDEX_NOT_NULL)
        @Min(value = 1, message = UPDATE_SUBJECTS_ORDER_INDEX_MIN)
        Integer index;

        @Builder
        public SubjectParam(Long id, Integer index) {
            this.id = id;
            this.index = index;
            this.validateSelf();
        }
    }
}
