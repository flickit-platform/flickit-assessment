package org.flickit.assessment.kit.application.port.in.expertgroup;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.springframework.web.multipart.MultipartFile;

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

        @NotBlank(message = UPDATE_EXPERT_GROUP_TITLE_NOT_BLANK)
        @Size(min = 3, message = UPDATE_EXPERT_GROUP_TITLE_SIZE_MIN)
        @Size(max = 100, message = UPDATE_EXPERT_GROUP_TITLE_SIZE_MAX)
        String title;

        @NotBlank(message = UPDATE_EXPERT_GROUP_BIO_NOT_BLANK)
        @Size(min = 3, message = UPDATE_EXPERT_GROUP_BIO_SIZE_MIN)
        @Size(max = 200, message = UPDATE_EXPERT_GROUP_BIO_SIZE_MAX)
        String bio;

        @NotBlank(message = UPDATE_EXPERT_GROUP_ABOUT_NOT_BLANK)
        @Size(min = 3, message = UPDATE_EXPERT_GROUP_ABOUT_SIZE_MIN)
        @Size(max = 500, message = UPDATE_EXPERT_GROUP_ABOUT_SIZE_MAX)
        String about;

        MultipartFile picture;

        @Size(min = 3, message = UPDATE_EXPERT_GROUP_WEBSITE_SIZE_MIN)
        @Size(max = 200, message = UPDATE_EXPERT_GROUP_WEBSITE_SIZE_MAX)
        String website;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        public Param(Long id, String name, String bio, String about, MultipartFile picture, String website, UUID currentUserId) {
            this.id = id;
            this.title = name != null ? name.strip() : null;
            this.bio = bio;
            this.about = about;
            this.picture = picture;
            this.website = website;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }
}
