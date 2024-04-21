package org.flickit.assessment.kit.application.port.in.attribute;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_KIT_ATTRIBUTE_DETAIL_ATTRIBUTE_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_KIT_ATTRIBUTE_DETAIL_KIT_ID_NOT_NULL;

public interface GetKitAttributeDetailUseCase {

    Result getKitAttributeDetail(Param param);

    @Value
    @EqualsAndHashCode(callSuper = true)
    class Param extends SelfValidating<Param> {

        @NotNull(message = GET_KIT_ATTRIBUTE_DETAIL_KIT_ID_NOT_NULL)
        Long kitId;

        @NotNull(message = GET_KIT_ATTRIBUTE_DETAIL_ATTRIBUTE_ID_NOT_NULL)
        Long attributeId;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        public Param(Long kitId, Long attributeId, UUID currentUserId) {
            this.kitId = kitId;
            this.attributeId = attributeId;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }

    record Result(Long id,
                  Integer index,
                  String title,
                  Integer questionCount,
                  Integer weight,
                  String description,
                  List<MaturityLevel> maturityLevels) {
    }

    record MaturityLevel(Long id,
                         Integer index,
                         String title,
                         Integer questionCount) {
    }
}
