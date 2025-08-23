package org.flickit.assessment.kit.application.port.in.attribute;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.DELETE_ATTRIBUTE_ATTRIBUTE_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.DELETE_ATTRIBUTE_KIT_VERSION_ID_NOT_NULL;

public interface DeleteAttributeUseCase {

    void deleteAttribute(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = DELETE_ATTRIBUTE_KIT_VERSION_ID_NOT_NULL)
        Long kitVersionId;

        @NotNull(message = DELETE_ATTRIBUTE_ATTRIBUTE_ID_NOT_NULL)
        Long attributeId;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Builder
        public Param(Long kitVersionId, Long attributeId, UUID currentUserId) {
            this.kitVersionId = kitVersionId;
            this.attributeId = attributeId;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }
}
