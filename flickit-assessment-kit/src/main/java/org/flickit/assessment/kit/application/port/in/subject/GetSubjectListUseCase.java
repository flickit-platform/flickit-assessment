package org.flickit.assessment.kit.application.port.in.subject;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_SUBJECT_LIST_KIT_VERSION_ID_NOT_NULL;

public interface GetSubjectListUseCase {

    PaginatedResponse<SubjectListItem> getSubjectList();

    @Value
    @EqualsAndHashCode(callSuper = true)
    class Param extends SelfValidating<Param> {

        @NotNull(message = GET_SUBJECT_LIST_KIT_VERSION_ID_NOT_NULL)
        Long kitVersionId;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Builder
        public Param(Long kitVersionId, UUID currentUserId) {
            this.kitVersionId = kitVersionId;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }

    record SubjectListItem(
        Long id,
        String title,
        String description,
        Integer index,
        Integer weight
    ) {
    }
}
