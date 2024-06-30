package org.flickit.assessment.users.application.port.in.expertgroup;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.hibernate.validator.constraints.URL;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.users.common.ErrorMessageKey.*;

public interface UpdateExpertGroupUseCase {

    void updateExpertGroup(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = UPDATE_EXPERT_GROUP_EXPERT_GROUP_ID_NOT_NULL)
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

        @URL(message = UPDATE_EXPERT_GROUP_WEBSITE_NOT_URL)
        @Size(min = 3, message = UPDATE_EXPERT_GROUP_WEBSITE_SIZE_MIN)
        @Size(max = 200, message = UPDATE_EXPERT_GROUP_WEBSITE_SIZE_MAX)
        String website;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        public Param(Long id, String title, String bio, String about, String website, UUID currentUserId) {
            this.id = id;
            this.title = title;
            this.bio = bio;
            this.about = about;
            this.website = (website != null && !website.isBlank()) ? website.strip() : null;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }
}
