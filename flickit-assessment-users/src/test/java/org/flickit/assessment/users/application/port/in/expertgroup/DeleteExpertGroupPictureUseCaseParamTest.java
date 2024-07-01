package org.flickit.assessment.users.application.port.in.expertgroup;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.users.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.*;

class DeleteExpertGroupPictureUseCaseParamTest {

    @Test
    void testDeleteExpertGroupPictureParam_idIsNull_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new DeleteExpertGroupPictureUseCase.Param(null, currentUserId));
        assertThat(throwable).hasMessage("expertGroupId: " + DELETE_EXPERT_GROUP_PICTURE_EXPERT_GROUP_ID_NOT_NULL);
    }

    @Test
    void testDeleteExpertGroupPictureParam_currentUserIdIsNull_ErrorMessage() {
        long expertGroupId = 0L;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new DeleteExpertGroupPictureUseCase.Param(expertGroupId, null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

}
