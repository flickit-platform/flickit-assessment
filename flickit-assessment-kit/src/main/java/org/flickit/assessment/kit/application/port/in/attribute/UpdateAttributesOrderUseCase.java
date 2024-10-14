package org.flickit.assessment.kit.application.port.in.attribute;

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

public interface UpdateAttributesOrderUseCase {

    void updateAttributesOrder(Param param);

    @Value
    @EqualsAndHashCode(callSuper = true)
    class Param extends SelfValidating<Param> {

        @NotNull(message = UPDATE_ATTRIBUTES_ORDER_KIT_VERSION_ID_NOT_NULL)
        Long kitVersionId;

        @NotNull(message = UPDATE_ATTRIBUTES_ORDER_ATTRIBUTES_NOT_NULL)
        @Size(min = 2, message = UPDATE_ATTRIBUTES_ORDER_ATTRIBUTES_SIZE_MIN)
        List<AttributeParam> attributes;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Builder
        public Param(Long kitVersionId, List<AttributeParam> attributes, UUID currentUserId) {
            this.kitVersionId = kitVersionId;
            this.attributes = attributes;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }

    @Value
    @EqualsAndHashCode(callSuper = true)
    class AttributeParam extends SelfValidating<AttributeParam> {

        @NotNull(message = UPDATE_ATTRIBUTES_ORDER_ATTRIBUTE_ID_NOT_NULL)
        Long id;

        @NotNull(message = UPDATE_ATTRIBUTES_ORDER_INDEX_NOT_NULL)
        @Min(value = 1, message = UPDATE_ATTRIBUTES_ORDER_INDEX_MIN)
        Integer index;

        @Builder
        public AttributeParam(Long id, Integer index) {
            this.id = id;
            this.index = index;
            this.validateSelf();
        }
    }
}
