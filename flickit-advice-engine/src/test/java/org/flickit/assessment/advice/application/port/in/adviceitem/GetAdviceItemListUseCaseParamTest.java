package org.flickit.assessment.advice.application.port.in.adviceitem;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.advice.common.ErrorMessageKey.*;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.*;

class GetAdviceItemListUseCaseParamTest {

    @Test
    void testGetAdviceItemListParam_assessmentIdParamViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.assessmentId(null)));
        assertThat(throwable).hasMessage("assessmentId: " + GET_ADVICE_ITEM_LIST_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void testGetAdviceItemListParam_pageParamViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.page(-1)));
        assertThat(throwable).hasMessage("page: " + GET_ADVICE_ITEM_LIST_PAGE_MIN);
    }

    @Test
    void testGetAdviceItemListParam_sizeParamViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.size(0)));
        assertThat(throwable).hasMessage("size: " + GET_ADVICE_ITEM_LIST_SIZE_MIN);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.size(101)));
        assertThat(throwable).hasMessage("size: " + GET_ADVICE_ITEM_LIST_SIZE_MAX);
    }

    @Test
    void testGetAdviceItemListParam_currentUserIdParamViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<GetAdviceItemListUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private GetAdviceItemListUseCase.Param.ParamBuilder paramBuilder() {
        return GetAdviceItemListUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .page(0)
            .size(50)
            .currentUserId(UUID.randomUUID());
    }
}
