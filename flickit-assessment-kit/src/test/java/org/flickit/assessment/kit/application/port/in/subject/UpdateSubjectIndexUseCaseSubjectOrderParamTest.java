package org.flickit.assessment.kit.application.port.in.subject;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UpdateSubjectIndexUseCaseSubjectOrderParamTest {

    @Test
    void testUpdateSubjectIndexUseCaseSubjectOrderParam_subjectIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.subjectId(null)));
        assertThat(throwable).hasMessage("subjectId: " + UPDATE_SUBJECT_INDEX_SUBJECT_ID_NOT_NULL);
    }

    @Test
    void testUpdateSubjectIndexUseCaseSubjectOrderParam_indexParamViolatesConstraints_ErrorMessage() {
        var throwableNullViolates = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.index(null)));
        assertThat(throwableNullViolates).hasMessage("index: " + UPDATE_SUBJECT_INDEX_INDEX_NOT_NULL);
        var throwableMinViolates = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.index(0)));
        assertThat(throwableMinViolates).hasMessage("index: " + UPDATE_SUBJECT_INDEX_INDEX_MIN);
    }

    private void createParam(Consumer<UpdateSubjectIndexUseCase.SubjectOrderParam.SubjectOrderParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private UpdateSubjectIndexUseCase.SubjectOrderParam.SubjectOrderParamBuilder paramBuilder() {
        return UpdateSubjectIndexUseCase.SubjectOrderParam.builder()
            .subjectId(2L)
            .index(3);
    }
}