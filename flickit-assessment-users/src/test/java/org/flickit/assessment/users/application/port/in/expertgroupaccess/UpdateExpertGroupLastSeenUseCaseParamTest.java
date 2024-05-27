package org.flickit.assessment.users.application.port.in.expertgroupaccess;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.users.common.ErrorMessageKey.UPDATE_EXPERT_GROUP_LAST_SEEN_EXPERT_GROUP_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UpdateExpertGroupLastSeenUseCaseParamTest {

    @Test
    void tesUpdateExpertGroupLastSeen_idIsNull_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateExpertGroupLastSeenUseCase.Param(null, currentUserId));
        assertThat(throwable).hasMessage("expertGroupId: " + UPDATE_EXPERT_GROUP_LAST_SEEN_EXPERT_GROUP_ID_NOT_NULL);
    }

    @Test
    void testUpdateExpertGroupLastSeen_currentUserIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateExpertGroupLastSeenUseCase.Param(123L, null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }
}
