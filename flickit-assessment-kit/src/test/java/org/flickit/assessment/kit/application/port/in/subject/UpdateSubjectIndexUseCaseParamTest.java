package org.flickit.assessment.kit.application.port.in.subject;

import jakarta.validation.ConstraintViolationException;
import org.flickit.assessment.kit.application.port.in.subject.UpdateSubjectIndexUseCase.Param;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UpdateSubjectIndexUseCaseParamTest {

    @Test
    void testUpdateSubjectIndexUseCaseParam_kitVersionIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.kitVersionId(null)));
        assertThat(throwable).hasMessage("kitVersionId: " + UPDATE_SUBJECT_INDEX_KIT_VERSION_ID_NOT_NULL);
    }

    @Test
    void testUpdateSubjectIndexUseCaseParam_subjectIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.subjectId(null)));
        assertThat(throwable).hasMessage("subjectId: " + UPDATE_SUBJECT_INDEX_SUBJECT_ID_NOT_NULL);
    }

    @Test
    void testUpdateSubjectIndexUseCaseParam_indexParamViolatesConstraints_ErrorMessage() {
        var throwableNullViolates = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.index(null)));
        assertThat(throwableNullViolates).hasMessage("index: " + UPDATE_SUBJECT_INDEX_INDEX_NOT_NULL);
        var throwableMinViolates = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.index(0)));
        assertThat(throwableMinViolates).hasMessage("index: " + UPDATE_SUBJECT_INDEX_INDEX_MIN);
    }

    @Test
    void testUpdateSubjectIndexUseCaseParam_currentUserParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private Param.ParamBuilder paramBuilder() {
        return Param.builder()
            .kitVersionId(1L)
            .subjectId(2L)
            .index(3)
            .currentUserId(UUID.randomUUID());
    }

}