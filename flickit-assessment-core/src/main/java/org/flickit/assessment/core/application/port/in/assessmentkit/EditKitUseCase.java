package org.flickit.assessment.core.application.port.in.assessmentkit;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.core.common.SelfValidating;

import java.util.UUID;

import static org.flickit.assessment.core.common.ErrorMessageKey.EDIT_KIT_CONTENT_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.EDIT_KIT_KIT_ID_NOT_NULL;

public interface EditKitUseCase {

    void edit(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = EDIT_KIT_KIT_ID_NOT_NULL)
        UUID kitId;

        @NotBlank(message = EDIT_KIT_CONTENT_NOT_NULL)
        String content;

        public Param(UUID kitId, String content) {
            this.kitId = kitId;
            this.content = content;
            this.validateSelf();
        }
    }
}
