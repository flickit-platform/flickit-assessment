package org.flickit.assessment.kit.application.port.in.subject;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.DELETE_SUBJECT_KIT_VERSION_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.DELETE_SUBJECT_SUBJECT_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DeleteSubjectUseCaseParamTest {

    @Test
    void testDeleteSubjectUseCaseParam_idParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.id(null)));
        assertThat(throwable).hasMessage("id: " + DELETE_SUBJECT_SUBJECT_ID_NOT_NULL);
    }

    @Test
    void testDeleteSubjectUseCaseParam_kitVersionIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.kitVersionId(null)));
        assertThat(throwable).hasMessage("kitVersionId: " + DELETE_SUBJECT_KIT_VERSION_ID_NOT_NULL);
    }

    @Test
    void testDeleteSubjectUseCaseParam_currentUserIdParamViolateConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<DeleteSubjectUseCase.Param.ParamBuilder> consumer) {
        var paramBuilder = paramBuilder();
        consumer.accept(paramBuilder);
        paramBuilder.build();
    }

    private DeleteSubjectUseCase.Param.ParamBuilder paramBuilder(){
        return DeleteSubjectUseCase.Param.builder()
            .id(1L)
            .kitVersionId(2L)
            .currentUserId(UUID.randomUUID());
    }
}
