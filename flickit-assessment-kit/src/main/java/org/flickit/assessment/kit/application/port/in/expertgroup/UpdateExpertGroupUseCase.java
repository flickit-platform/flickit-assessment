package org.flickit.assessment.kit.application.port.in.expertgroup;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;

public interface UpdateExpertGroupUseCase {

    void updateExpertGroup(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = UPDATE_EXPERT_GROUP_ID_NOT_NULL)
        Long id;

        @NotBlank(message = UPDATE_EXPERT_GROUP_NAME_NOT_BLANK)
        @Size(min = 3, message = UPDATE_EXPERT_GROUP_NAME_SIZE_MIN)
        @Size(max = 100, message = UPDATE_EXPERT_GROUP_NAME_SIZE_MAX)
        String name;

        @Size(max = 200, message = UPDATE_EXPERT_GROUP_BIO_SIZE_MAX)
        @NotBlank(message = UPDATE_EXPERT_GROUP_BIO_NOT_BLANK)
        String bio;

        @Size(max = 500, message = UPDATE_EXPERT_GROUP_ABOUT_SIZE_MAX)
        @NotBlank(message = UPDATE_EXPERT_GROUP_ABOUT_NOT_BLANK)
        String about;

        @Size(max = 200, message = UPDATE_EXPERT_GROUP_WEBSITE_SIZE_MAX)
        @NotBlank(message = UPDATE_EXPERT_GROUP_WEBSITE_NOT_BLANK)
        String website;

        @Size(max = 100, message = UPDATE_EXPERT_GROUP_PICTURE_SIZE_MAX)
        @NotBlank(message = UPDATE_EXPERT_GROUP_PICTURE_NOT_BLANK)
        String picture;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        public Param(Long id, String name, String bio, String about, String website, String picture, UUID currentUserId) {
            this.id = id;
            this.name = name != null ? name.strip() : null;
            this.bio = bio;
            this.about = about;
            this.website = website;
            this.picture = picture;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }
}
