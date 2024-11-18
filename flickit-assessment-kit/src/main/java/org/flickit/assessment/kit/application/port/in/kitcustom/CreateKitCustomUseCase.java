package org.flickit.assessment.kit.application.port.in.kitcustom;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.common.exception.ValidationException;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;

public interface CreateKitCustomUseCase {

    long createKitCustom(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = CREATE_KIT_CUSTOM_KIT_ID_NOT_NULL)
        Long kitId;

        @NotNull(message = CREATE_KIT_CUSTOM_TITLE_NOT_NULL)
        @Size(min = 3, message = CREATE_KIT_CUSTOM_TITLE_SIZE_MIN)
        @Size(max = 100, message = CREATE_KIT_CUSTOM_TITLE_SIZE_MAX)
        String title;

        @NotNull(message = CREATE_KIT_CUSTOM_DATA_NOT_NULL)
        KitCustomData customData;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Builder
        public Param(Long kitId, String title, KitCustomData customData, UUID currentUserId) {
            this.kitId = kitId;
            this.title = title != null && !title.isBlank() ? title.trim() : null;
            this.customData = customData;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }

        public record KitCustomData(List<CustomSubject> customSubjects, List<CustomAttribute> customAttributes) {

            public KitCustomData {
                if (customSubjects.isEmpty() && customAttributes.isEmpty())
                    throw new ValidationException(CREATE_KIT_CUSTOM_EMPTY_CUSTOM_NOT_ALLOWED);
            }

            @Value
            @EqualsAndHashCode(callSuper = false)
            public static class CustomSubject extends SelfValidating<CustomSubject> {

                @NotNull(message = CREATE_KIT_CUSTOM_SUBJECT_ID_NOT_NULL)
                Long id;

                @NotNull(message = CREATE_KIT_CUSTOM_SUBJECT_WEIGHT_NOT_NULL)
                Integer weight;

                @Builder
                public CustomSubject(Long id, Integer weight) {
                    this.id = id;
                    this.weight = weight;
                    this.validateSelf();
                }
            }

            @Value
            @EqualsAndHashCode(callSuper = false)
            public static class CustomAttribute extends SelfValidating<CustomAttribute> {

                @NotNull(message = CREATE_KIT_CUSTOM_ATTRIBUTE_ID_NOT_NULL)
                Long id;

                @NotNull(message = CREATE_KIT_CUSTOM_ATTRIBUTE_WEIGHT_NOT_NULL)
                Integer weight;

                public CustomAttribute(Long id, Integer weight) {
                    this.id = id;
                    this.weight = weight;
                    this.validateSelf();
                }
            }
        }
    }
}
