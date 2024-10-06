package org.flickit.assessment.kit.application.port.in.subject;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_SUBJECT_LIST_KIT_VERSION_ID_NOT_NULL;
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

    private void createParam(Consumer<GetSubjectListUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private GetSubjectListUseCase.Param.ParamBuilder paramBuilder() {
        return GetSubjectListUseCase.Param.builder()
            .kitVersionId(1L)
            .currentUserId(UUID.randomUUID());
    }
}
