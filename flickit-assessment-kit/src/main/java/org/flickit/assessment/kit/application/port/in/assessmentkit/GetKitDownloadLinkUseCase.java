package org.flickit.assessment.kit.application.port.in.assessmentkit;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import static org.flickit.assessment.kit.common.ErrorMessageKey.*;

public interface GetKitDownloadLinkUseCase {

    String getKitLink(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = GET_KIT_DSL_DOWNLOAD_LINK_KIT_ID_NOT_NULL)
        Long kitId;

        public Param(Long kitId) {
            this.kitId = kitId;
        }
    }
}
