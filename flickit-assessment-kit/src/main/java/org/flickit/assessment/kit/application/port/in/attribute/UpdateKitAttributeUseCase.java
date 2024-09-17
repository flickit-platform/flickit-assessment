package org.flickit.assessment.kit.application.port.in.attribute;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;

public interface UpdateKitAttributeUseCase {

    void updateKitAttribute(Param param);

    @Value
    @EqualsAndHashCode(callSuper = true)
    class Param extends SelfValidating<Param> {

        @NotNull(message = UPDATE_KIT_ATTRIBUTE_KIT_ID_NOT_NULL)
        Long kitId;

        @NotNull(message = UPDATE_KIT_ATTRIBUTE_ATTRIBUTE_ID_NOT_NULL)
        Long attributeId;

        @NotBlank(message = UPDATE_KIT_ATTRIBUTE_CODE_NOT_BLANK)
        @Size(min = 3, message = UPDATE_KIT_ATTRIBUTE_CODE_SIZE_MIN)
        @Size(max = 50, message = UPDATE_KIT_ATTRIBUTE_CODE_SIZE_MAX)
        String code;

        @NotBlank(message = UPDATE_KIT_ATTRIBUTE_TITLE_NOT_BLANK)
        @Size(min = 3, message = UPDATE_KIT_ATTRIBUTE_TITLE_SIZE_MIN)
        @Size(max = 100, message = UPDATE_KIT_ATTRIBUTE_TITLE_SIZE_MAX)
        String title;

        @NotBlank(message = UPDATE_KIT_ATTRIBUTE_DESCRIPTION_NOT_BLANK)
        @Size(min = 3, message = UPDATE_KIT_ATTRIBUTE_DESCRIPTION_SIZE_MIN)
        String description;

        @NotNull(message = UPDATE_KIT_ATTRIBUTE_SUBJECT_ID_NOT_NULL)
        Long subjectId;

        @NotNull(message = UPDATE_KIT_ATTRIBUTE_INDEX_NOT_NULL)
        Integer index;

        @NotNull(message = UPDATE_KIT_ATTRIBUTE_WEIGHT_NOT_NULL)
        @Min(value = 1, message = UPDATE_KIT_ATTRIBUTE_WEIGHT_MIN)
        Integer weight;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        public Param(Long kitId,
                     Long attributeId,
                     String code,
                     String title,
                     String description,
                     Long subjectId,
                     Integer index,
                     Integer weight,
                     UUID currentUserId) {
            this.kitId = kitId;
            this.attributeId = attributeId;
            this.code = code;
            this.title = title;
            this.description = description;
            this.subjectId = subjectId;
            this.index = index;
            this.weight = weight;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }
}
