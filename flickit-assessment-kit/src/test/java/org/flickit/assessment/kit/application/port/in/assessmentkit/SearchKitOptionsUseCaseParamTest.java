package org.flickit.assessment.kit.application.port.in.assessmentkit;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SearchKitOptionsUseCaseParamTest {

    @Test
    void testSearchKitOptions_QueryIsNull_Success() {
        UUID currentUserId = UUID.randomUUID();
        assertDoesNotThrow(() -> new SearchKitOptionsUseCase.Param(null,0, 10, currentUserId));
    }

    @Test
    void testSearchKitOptions_PageIsLessThanMin_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new SearchKitOptionsUseCase.Param("",-1, 1, currentUserId));
        assertThat(throwable).hasMessage("page: " + SEARCH_KIT_OPTIONS_PAGE_MIN);
    }

    @Test
    void testSearchKitOptions_SizeIsLessThanMin_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new SearchKitOptionsUseCase.Param("",0, -1, currentUserId));
        assertThat(throwable).hasMessage("size: " + SEARCH_KIT_OPTION_SIZE_MIN);
    }

    @Test
    void testSearchKitOptions_SizeIsMoreThanMax_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new SearchKitOptionsUseCase.Param("",0, 101, currentUserId));
        assertThat(throwable).hasMessage("size: " + SEARCH_KIT_OPTIONS_SIZE_MAX);
    }

    @Test
    void testSearchKitOptions_currentUserIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new SearchKitOptionsUseCase.Param("",0, 10, null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }
}
