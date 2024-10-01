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

public interface UpdateSubjectIndexUseCase {

    void updateSubjectIndex(Param param);

    @Value
    @EqualsAndHashCode(callSuper = true)
    class Param extends SelfValidating<Param> {

        @NotNull(message = UPDATE_SUBJECT_INDEX_KIT_VERSION_ID_NOT_NULL)
        Long kitVersionId;

        @NotNull(message = UPDATE_SUBJECT_INDEX_SUBJECT_ORDERS_NOT_NULL)
        @Size(min = 1, message = UPDATE_SUBJECT_INDEX_SUBJECT_ORDERS_MIN)
        List<SubjectOrderParam> subjectOrders;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Builder
        public Param(Long kitVersionId, List<SubjectOrderParam> subjectOrders, UUID currentUserId) {
            this.kitVersionId = kitVersionId;
            this.subjectOrders = subjectOrders;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }

    @Value
    @EqualsAndHashCode(callSuper = true)
    class SubjectOrderParam extends SelfValidating<SubjectOrderParam> {

        @NotNull(message = UPDATE_SUBJECT_INDEX_SUBJECT_ID_NOT_NULL)
        Long subjectId;

        @NotNull(message = UPDATE_SUBJECT_INDEX_INDEX_NOT_NULL)
        @Min(value = 1, message = UPDATE_SUBJECT_INDEX_INDEX_MIN)
        Integer index;

        @Builder
        public SubjectOrderParam(Long subjectId, Integer index) {
            this.subjectId = subjectId;
            this.index = index;
            this.validateSelf();
        }
    }
}
