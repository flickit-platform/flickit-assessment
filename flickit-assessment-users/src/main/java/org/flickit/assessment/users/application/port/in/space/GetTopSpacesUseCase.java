package org.flickit.assessment.users.application.port.in.space;


import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.common.application.domain.space.SpaceType;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;

public interface GetTopSpacesUseCase {

    List<SpaceListItem> getSpaceList(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Builder
        public Param(UUID currentUserId) {
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }

    record SpaceListItem(long id,
                         String title,
                         SpaceType type,
                         boolean isDefault) {
    }
}
