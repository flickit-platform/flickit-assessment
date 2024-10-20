package org.flickit.assessment.users.application.port.in.expertgroup;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.users.common.ErrorMessageKey;
import org.hibernate.validator.constraints.URL;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;

public interface CreateExpertGroupUseCase {

    Result createExpertGroup(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotBlank(message = ErrorMessageKey.CREATE_EXPERT_GROUP_TITLE_NOT_BLANK)
        @Size(min = 3, message = ErrorMessageKey.CREATE_EXPERT_GROUP_TITLE_SIZE_MIN)
        @Size(max = 100, message = ErrorMessageKey.CREATE_EXPERT_GROUP_TITLE_SIZE_MAX)
        String title;

        @NotBlank(message = ErrorMessageKey.CREATE_EXPERT_GROUP_BIO_NOT_BLANK)
        @Size(min = 3, message = ErrorMessageKey.CREATE_EXPERT_GROUP_BIO_SIZE_MIN)
        @Size(max = 200, message = ErrorMessageKey.CREATE_EXPERT_GROUP_BIO_SIZE_MAX)
        String bio;

        @NotBlank(message = ErrorMessageKey.CREATE_EXPERT_GROUP_ABOUT_NOT_BLANK)
        @Size(min = 3, message = ErrorMessageKey.CREATE_EXPERT_GROUP_ABOUT_SIZE_MIN)
        @Size(max = 500, message = ErrorMessageKey.CREATE_EXPERT_GROUP_ABOUT_SIZE_MAX)
        String about;

        @URL(message = ErrorMessageKey.CREATE_EXPERT_GROUP_WEBSITE_NOT_URL)
        @Size(min = 3, message = ErrorMessageKey.CREATE_EXPERT_GROUP_WEBSITE_SIZE_MIN)
        @Size(max = 200, message = ErrorMessageKey.CREATE_EXPERT_GROUP_WEBSITE_SIZE_MAX)
        String website;

        MultipartFile picture;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        public Param(String title, String bio, String about, MultipartFile picture, String website, UUID currentUserId) {
            this.title = title != null ? title.strip() : null;
            this.bio = bio;
            this.about = about;
            this.picture = picture;
            this.website = (website != null && !website.isBlank()) ? website.strip() : null;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }

    record Result(Long id) {
    }
}
