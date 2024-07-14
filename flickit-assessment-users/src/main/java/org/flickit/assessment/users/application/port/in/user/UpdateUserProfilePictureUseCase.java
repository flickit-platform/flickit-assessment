package org.flickit.assessment.users.application.port.in.user;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.users.common.ErrorMessageKey.UPDATE_USER_PROFILE_PICTURE_PICTURE_NOT_NULL;

public interface UpdateUserProfilePictureUseCase {

    Result updateUserProfilePicture(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID userId;

        @NotNull(message = UPDATE_USER_PROFILE_PICTURE_PICTURE_NOT_NULL)
        MultipartFile picture;

        public Param(UUID userId, MultipartFile picture) {
            this.userId = userId;
            this.picture = picture == null || picture.isEmpty() ? null : picture;
            this.validateSelf();
        }
    }

    record Result(String pictureLink) {
    }
}
