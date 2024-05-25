package org.flickit.assessment.kit.application.port.in.assessmentkit;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GetExpertGroupKitListUseCaseParamTest {

    @Test
    void testGetExpertGroupKitList_ExpertGroupIdIsNull_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetExpertGroupKitListUseCase.Param(null, 0, 10, currentUserId));
        assertThat(throwable).hasMessage("expertGroupId: " + GET_EXPERT_GROUP_KIT_LIST_EXPERT_GROUP_ID_NOT_NULL);
    }

    @Test
    void testGetExpertGroupKitList_PageIsLessThanMin_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetExpertGroupKitListUseCase.Param(1L, -1, 10, currentUserId));
        assertThat(throwable).hasMessage("page: " + GET_EXPERT_GROUP_KIT_LIST_PAGE_MIN);
    }

    @Test
    void testGetExpertGroupKitList_SizeIsLessThanMin_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetExpertGroupKitListUseCase.Param(1L, 0, -1, currentUserId));
        assertThat(throwable).hasMessage("size: " + GET_EXPERT_GROUP_KIT_LIST_SIZE_MIN);
    }

    @Test
    void testGetExpertGroupKitList_SizeIsMoreThanMax_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetExpertGroupKitListUseCase.Param(1L, 0, 101, currentUserId));
        assertThat(throwable).hasMessage("size: " + GET_EXPERT_GROUP_KIT_LIST_SIZE_MAX);
    }

    @Test
    void testGetExpertGroupKitList_currentUserIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetExpertGroupKitListUseCase.Param(1L, 0, 10, null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }
}
