package org.flickit.assessment.kit.application.port.in.assessmentkit;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_KIT_MINIMAL_INFO_KIT_ID_NOT_NULL;

public interface GetKitMinimalInfoUseCase {

    Result getKitMinimalInfo(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<GetKitMinimalInfoUseCase.Param> {

        @NotNull(message = GET_KIT_MINIMAL_INFO_KIT_ID_NOT_NULL)
        Long kitId;

        public Param(Long kitId) {
            this.kitId = kitId;
            this.validateSelf();
        }
    }

    record Result(Long id, String title, MinimalExpertGroup minimalExpertGroup) {}
    record MinimalExpertGroup(Long id, String title) {}
}
