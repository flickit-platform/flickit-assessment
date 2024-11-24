package org.flickit.assessment.kit.application.port.in.kitcustom;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_KIT_CUSTOM_KIT_CUSTOM_ID_NOT_NULL;

public interface GetKitCustomUseCase {

    Result getKitCustom(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = GET_KIT_CUSTOM_KIT_CUSTOM_ID_NOT_NULL)
        Long kitCustomId;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Builder
        public Param(Long kitCustomId, UUID currentUserId) {
            this.kitCustomId = kitCustomId;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }

    record Result(String title, ResultCustomData customData) {

        public record ResultCustomData(List<Data> subjects, List<Data> attributes) {

            public record Data(long id, int weight) {
            }
        }
    }
}
