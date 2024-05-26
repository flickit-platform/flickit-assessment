package org.flickit.assessment.kit.application.port.in.kittag;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GetKitTagListUseCaseParamTest {

    @Test
    void testGetKitTagList_PageIsLessThanMin_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetKitTagListUseCase.Param(-1, 1));
        assertThat(throwable).hasMessage("page: " + GET_KIT_TAG_LIST_PAGE_MIN);
    }

    @Test
    void testGetKitTagList_SizeIsLessThanMin_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetKitTagListUseCase.Param(0, -1));
        assertThat(throwable).hasMessage("size: " + GET_KIT_TAG_LIST_SIZE_MIN);
    }

    @Test
    void testGetKitTagList_SizeIsMoreThanMax_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetKitTagListUseCase.Param(0, 101));
        assertThat(throwable).hasMessage("size: " + GET_KIT_TAG_LIST_SIZE_MAX);
    }
}
