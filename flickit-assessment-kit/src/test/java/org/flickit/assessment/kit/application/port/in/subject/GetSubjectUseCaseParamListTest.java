package org.flickit.assessment.kit.application.port.in.subject;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.*;

class GetSubjectUseCaseParamListTest {

    @Test
    void testGetSubjectListUseCaseParam_kitVersionIdViolateConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.kitVersionId(null)));
        assertThat(throwable).hasMessage("kitVersionId: " + GET_SUBJECT_LIST_KIT_VERSION_ID_NOT_NULL);
    }

    @Test
    void testGetSubjectListUseCaseParam_currentUserIdViolateConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    @Test
    void testGetSubjectListUseCaseParam_sizeViolateConstraints_ErrorMessage() {
        var throwableMin = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.size(0)));
        assertThat(throwableMin).hasMessage("size: " + GET_SUBJECT_LIST_SIZE_MIN);

        var throwableMax = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.size(51)));
        assertThat(throwableMax).hasMessage("size: " + GET_SUBJECT_LIST_SIZE_MAX);
    }

    @Test
    void testGetSubjectListUseCaseParam_pageViolateConstraints_ErrorMessage() {
        var throwableMin = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.page(-1)));
        assertThat(throwableMin).hasMessage("page: " + GET_SUBJECT_LIST_PAGE_MIN);
    }

    private void createParam(Consumer<GetSubjectListUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private GetSubjectListUseCase.Param.ParamBuilder paramBuilder() {
        return GetSubjectListUseCase.Param.builder()
            .kitVersionId(1L)
            .size(1)
            .page(10)
            .currentUserId(UUID.randomUUID());
    }
}
