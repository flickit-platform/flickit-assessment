package org.flickit.assessment.kit.application.port.in.assessmentkit;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import static org.flickit.assessment.kit.common.ErrorMessageKey.UPDATE_KIT_BY_DSL_DSL_CONTENT_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.UPDATE_KIT_BY_DSL_KIT_ID_NOT_NULL;

public interface UpdateKitByDslUseCase {

    void update(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = UPDATE_KIT_BY_DSL_KIT_ID_NOT_NULL)
        Long kitId;

        @NotBlank(message = UPDATE_KIT_BY_DSL_DSL_CONTENT_NOT_NULL)
        String dslContent;

        public Param(Long kitId, String dslContent) {
            this.kitId = kitId;
            this.dslContent = dslContent;
            this.validateSelf();
        }
    }
}
