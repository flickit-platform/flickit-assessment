package org.flickit.assessment.kit.application.port.in.subject;

import jakarta.validation.ConstraintViolationException;
import org.flickit.assessment.kit.application.port.in.subject.UpdateSubjectsOrderUseCase.SubjectParam;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UpdateSubjectsOrdersUseCaseSubjectParamTest {

    @Test
    void testUpdateSubjectsOrderUseCaseSubjectParam_subjectIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.id(null)));
        assertThat(throwable).hasMessage("id: " + UPDATE_SUBJECTS_ORDER_SUBJECT_ID_NOT_NULL);
    }

    @Test
    void testUpdateSubjectsOrderUseCaseSubjectParam_indexParamViolatesConstraints_ErrorMessage() {
        var throwableNullViolates = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.index(null)));
        assertThat(throwableNullViolates).hasMessage("index: " + UPDATE_SUBJECTS_ORDER_INDEX_NOT_NULL);
        var throwableMinViolates = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.index(0)));
        assertThat(throwableMinViolates).hasMessage("index: " + UPDATE_SUBJECTS_ORDER_INDEX_MIN);
    }

    private void createParam(Consumer<SubjectParam.SubjectParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private SubjectParam.SubjectParamBuilder paramBuilder() {
        return SubjectParam.builder()
            .id(2L)
            .index(3);
    }
}