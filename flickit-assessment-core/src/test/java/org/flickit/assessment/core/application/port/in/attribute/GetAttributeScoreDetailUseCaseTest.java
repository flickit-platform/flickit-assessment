package org.flickit.assessment.core.application.port.in.attribute;

import jakarta.validation.ConstraintViolationException;
import org.flickit.assessment.common.application.domain.crud.Order;
import org.flickit.assessment.common.application.domain.crud.Sort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_ATTRIBUTE_SCORE_DETAIL_PAGE_MIN;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GetAttributeScoreDetailUseCaseTest {

    @Test
    void testGetAttributeScoreDetailUseCaseParam_AttributeLevelIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.attributeId(null)));
        assertThat(throwable).hasMessage("attributeId: " + GET_ATTRIBUTE_SCORE_DETAIL_ATTRIBUTE_ID_NOT_NULL);
    }

    @Test
    void testGetAttributeScoreDetailUseCaseParam_MaturityLevelIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.maturityLevelId(null)));
        assertThat(throwable).hasMessage("maturityLevelId: " + GET_ATTRIBUTE_SCORE_DETAIL_MATURITY_LEVEL_ID_NOT_NULL);
    }

    @Test
    void testGetAttributeScoreDetailUseCaseParam_sortParamViolatesConstrains_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.sort("invalid_sort")));
        assertThat(throwable).hasMessage("sort: " + GET_ATTRIBUTE_SCORE_DETAIL_SORT_INVALID);

        assertDoesNotThrow(() -> createParam(b -> b.sort("    weight     ")));
        assertDoesNotThrow(() -> createParam(b -> b.sort("    SCORE     ")));
        assertDoesNotThrow(() -> createParam(b -> b.sort("    FiNal_ScoRe     ")));
        assertDoesNotThrow(() -> createParam(b -> b.sort("    confIDence     ")));
        var params = assertDoesNotThrow(() -> createParam(b -> b.sort(null)));
        assertEquals(Sort.DEFAULT.name(), params.getSort());
    }

    @Test
    void testGetAttributeScoreDetailUseCaseParam_orderParamViolatesConstrains_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.order("invalid_order")));
        assertThat(throwable).hasMessage("order: " + GET_ATTRIBUTE_SCORE_DETAIL_ORDER_INVALID);


        assertDoesNotThrow(() -> createParam(b -> b.order("    asc     ")));
        assertDoesNotThrow(() -> createParam(b -> b.order("    DESC     ")));
        var params = assertDoesNotThrow(() -> createParam(b -> b.order(null)));
        assertEquals(Order.DEFAULT.name(), params.getOrder());
    }

    @Test
    void testGetAttributeScoreDetailUseCaseParam_sizeParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.size(-1)));
        assertThat(throwable).hasMessage("size: " + GET_ATTRIBUTE_SCORE_DETAIL_SIZE_MIN);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.size(101)));
        assertThat(throwable).hasMessage("size: " + GET_ATTRIBUTE_SCORE_DETAIL_SIZE_MAX);
    }

    @Test
    void testGetAttributeScoreDetailUseCaseParam_pageParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.page(-1)));
        assertThat(throwable).hasMessage("page: " + GET_ATTRIBUTE_SCORE_DETAIL_PAGE_MIN);
    }

    @Test
    void testGetAttributeScoreDetailUseCaseParam_currentUserIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private GetAttributeScoreDetailUseCase.Param createParam(Consumer<GetAttributeScoreDetailUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private GetAttributeScoreDetailUseCase.Param.ParamBuilder paramBuilder() {
        return GetAttributeScoreDetailUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .attributeId(1L)
            .maturityLevelId(1L)
            .size(50)
            .page(0)
            .sort("weight")
            .order("asc")
            .currentUserId(UUID.randomUUID());
    }
}
