package org.flickit.assessment.users.application.port.in.expertgroup;

import org.flickit.assessment.users.application.domain.ExpertGroup;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.users.common.ErrorMessageKey.GET_EXPERT_GROUP_EXPERT_GROUP_ID_NOT_NULL;

public interface GetExpertGroupUseCase {

    Result getExpertGroup(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = GET_EXPERT_GROUP_EXPERT_GROUP_ID_NOT_NULL)
        Long id;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;
    }

    record Result(ExpertGroup expertGroup, String pictureLink, boolean editable) {
    }
}
