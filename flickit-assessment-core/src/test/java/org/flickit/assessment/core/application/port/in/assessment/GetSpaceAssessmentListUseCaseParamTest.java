package org.flickit.assessment.core.application.port.in.assessment;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GetSpaceAssessmentListUseCaseParamTest {

    @Test
    void testGetSpaceAssessmentListUseCaseParam_SpaceIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.spaceId(null)));
        assertThat(throwable).hasMessage("spaceId: " + GET_SPACE_ASSESSMENT_LIST_SPACE_ID_NOT_NULL);
    }

    @Test
    void testGetSpaceAssessmentListUseCaseParam_CurrentUserIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    @Test
    void testGetSpaceAssessmentListUseCaseParam_PageSizeIsLessThanMin_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.size(0)));
        assertThat(throwable).hasMessage("size: " + GET_SPACE_ASSESSMENT_LIST_SIZE_MIN);
    }

    @Test
    void testGetSpaceAssessmentListUseCaseParam_PageSizeIsGreaterThanMax_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.size(101)));
        assertThat(throwable).hasMessage("size: " + GET_SPACE_ASSESSMENT_LIST_SIZE_MAX);
    }

    @Test
    void testGetSpaceAssessmentListUseCaseParam_PageNumberIsLessThanMin_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.page(-1)));
        assertThat(throwable).hasMessage("page: " + GET_SPACE_ASSESSMENT_LIST_PAGE_MIN);
    }

    private void createParam(Consumer<GetSpaceAssessmentListUseCase.Param.ParamBuilder> changer) {
        var param = paramBuilder();
        changer.accept(param);
        param.build();
    }

    private GetSpaceAssessmentListUseCase.Param.ParamBuilder paramBuilder() {
        return GetSpaceAssessmentListUseCase.Param.builder()
            .spaceId(123L)
            .currentUserId(UUID.randomUUID())
            .page(0)
            .size(10);
    }
}
