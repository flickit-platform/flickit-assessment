package org.flickit.assessment.kit.application.port.in.attribute;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;

public interface GetAttributesUseCase {

    PaginatedResponse<AttributeListItem> getAttributes(Param param);

    @Value
    @EqualsAndHashCode(callSuper = true)
    class Param extends SelfValidating<Param> {

        @NotNull(message = GET_ATTRIBUTES_KIT_VERSION_ID_NOT_NULL)
        Long kitVersionId;

        @Min(value = 0, message = GET_ATTRIBUTES_PAGE_MIN)
        int page;

        @Min(value = 1, message = GET_ATTRIBUTES_SIZE_MIN)
        @Max(value = 100, message = GET_ATTRIBUTES_SIZE_MAX)
        int size;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Builder
        public Param(Long kitVersionId, int page, int size, UUID currentUserId) {
            this.kitVersionId = kitVersionId;
            this.page = page;
            this.size = size;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }

    record AttributeListItem(long id, int index, String title, String description, int weight, AttributeSubject subject) {
    }

    record AttributeSubject(long id, String title) {
    }
}
