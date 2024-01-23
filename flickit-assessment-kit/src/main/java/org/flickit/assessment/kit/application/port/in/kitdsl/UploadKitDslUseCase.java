package org.flickit.assessment.kit.application.port.in.kitdsl;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.UPLOAD_KIT_DSL_EXPERT_GROUP_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.UPLOAD_KIT_DSL_KIT_NOT_NULL;

public interface UploadKitDslUseCase {

    Long upload(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = UPLOAD_KIT_DSL_KIT_NOT_NULL)
        MultipartFile dslFile;

        @NotNull(message = UPLOAD_KIT_DSL_EXPERT_GROUP_ID_NOT_NULL)
        Long expertGroupId;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        public Param(MultipartFile dslFile, Long expertGroupId, UUID currentUserId) {
            this.dslFile = dslFile;
            this.expertGroupId = expertGroupId;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }

}
