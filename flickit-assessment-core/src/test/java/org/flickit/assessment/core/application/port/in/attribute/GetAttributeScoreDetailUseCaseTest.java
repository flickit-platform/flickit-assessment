package org.flickit.assessment.core.application.port.in.attribute;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    void testGetAttributeScoreDetailUseCaseParam_orderParamViolatesConstrains_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.order("invalid_order")));
        assertThat(throwable).hasMessage("order: " + GET_ATTRIBUTE_SCORE_DETAIL_ORDER_INVALID);
    }

    @Test
    void testGetAttributeScoreDetailUseCaseParam_sortParamViolatesConstrains_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.sort("invalid_sort")));
        assertThat(throwable).hasMessage("sort: " + GET_ATTRIBUTE_SCORE_DETAIL_SORT_INVALID);
    }

    @Test
    void testGetAttributeScoreDetailUseCaseParam_currentUserIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<GetAttributeScoreDetailUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private GetAttributeScoreDetailUseCase.Param.ParamBuilder paramBuilder() {
        return GetAttributeScoreDetailUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .attributeId(1L)
            .maturityLevelId(1L)
            .sort("asc")
            .order("weight")
            .currentUserId(UUID.randomUUID());
    }
}
