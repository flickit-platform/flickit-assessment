package org.flickit.assessment.kit.application.port.in.assessmentkit;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.springframework.web.multipart.MultipartFile;

import static org.flickit.assessment.kit.common.ErrorMessageKey.UPLOAD_KIT_DSL_KIT_NOT_NULL;

public interface UploadKitUseCase {

    Long upload(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = UPLOAD_KIT_DSL_KIT_NOT_NULL)
        MultipartFile dslFile;

        public Param(MultipartFile dslFile) {
            this.dslFile = dslFile;
            this.validateSelf();
        }
    }

}
