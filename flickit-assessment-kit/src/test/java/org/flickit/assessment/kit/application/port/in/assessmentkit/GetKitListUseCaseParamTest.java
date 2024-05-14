package org.flickit.assessment.kit.application.port.in.assessmentkit;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GetKitListUseCaseParamTest {

    @Test
    void testGetAssessmentKitList_IsPrivateIsNull_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetKitListUseCase.Param(null, 0, 10, currentUserId));
        assertThat(throwable).hasMessage("isPrivate: " + GET_KIT_LIST_IS_PRIVATE_NOT_NULL);
    }

    @Test
    void testGetAssessmentKitList_PageIsLessThanMin_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetKitListUseCase.Param( true, -1, 10, currentUserId));
        assertThat(throwable).hasMessage("page: " + GET_KIT_LIST_PAGE_MIN);
    }

    @Test
    void testGetAssessmentKitList_SizeIsLessThanMin_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetKitListUseCase.Param(true, 0, -1, currentUserId));
        assertThat(throwable).hasMessage("size: " + GET_KIT_LIST_SIZE_MIN);
    }

    @Test
    void testGetAssessmentKitList_SizeIsMoreThanMax_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetKitListUseCase.Param(true,0, 101, currentUserId));
        assertThat(throwable).hasMessage("size: " + GET_KIT_LIST_SIZE_MAX);
    }

    @Test
    void testGetAssessmentKitList_currentUserIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetKitListUseCase.Param(true, 0, 10, null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }
}
