package org.flickit.assessment.users.application.port.in.expertgroup;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.users.common.ErrorMessageKey.*;

public interface UpdateExpertGroupPictureUseCase {

    Result update(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = UPDATE_EXPERT_GROUP_PICTURE_EXPERT_GROUP_ID_NOT_NULL)
        Long expertGroupId;

        @NotNull(message = UPDATE_EXPERT_GROUP_PICTURE_PICTURE_NOT_NULL)
        MultipartFile picture;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        public Param(Long expertGroupId, MultipartFile picture, UUID currentUserId) {
            this.expertGroupId = expertGroupId;
            this.picture = picture == null || picture.isEmpty() ? null : picture;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }

    record Result(String pictureLink) {
    }
}
