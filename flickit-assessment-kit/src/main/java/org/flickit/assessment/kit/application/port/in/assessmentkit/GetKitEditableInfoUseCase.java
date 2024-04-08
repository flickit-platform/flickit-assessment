package org.flickit.assessment.kit.application.port.in.assessmentkit;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.kit.application.domain.KitTag;

import java.util.List;

import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_KIT_EDITABLE_INFO_KIT_ID_NOT_NULL;

public interface GetKitEditableInfoUseCase {

    KitEditableInfo getKitEditableInfo(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = GET_KIT_EDITABLE_INFO_KIT_ID_NOT_NULL)
        Long kitId;

        public Param(Long kitId) {
            this.kitId = kitId;
            this.validateSelf();
        }
    }

    record KitEditableInfo(
        Long id,
        String title,
        String summary,
        Boolean isActive,
        Double price,
        String about,
        List<KitTag> tags) {
    }
}
