package org.flickit.assessment.kit.application.port.in.levelcompetence;

import jakarta.validation.ConstraintViolationException;
import org.flickit.assessment.kit.application.port.in.levelcompetence.CreateLevelCompetenceUseCase.Param;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CreateLevelCompetenceUseCaseParamTest {

    @Test
    void testCreateLevelCompetenceUseCaseParam_kitVersionIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.kitVersionId(null)));
        assertThat(throwable).hasMessage("kitVersionId: " + CREATE_LEVEL_COMPETENCE_KIT_VERSION_ID_NOT_NULL);
    }

    @Test
    void testCreateLevelCompetenceUseCaseParam_effectiveLevelIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.effectiveLevelId(null)));
        assertThat(throwable).hasMessage("effectiveLevelId: " + CREATE_LEVEL_COMPETENCE_EFFECTIVE_LEVEL_ID_NOT_NULL);
    }

    @Test
    void testCreateLevelCompetenceUseCaseParam_affectedLevelIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.affectedLevelId(null)));
        assertThat(throwable).hasMessage("affectedLevelId: " + CREATE_LEVEL_COMPETENCE_AFFECTED_LEVEL_ID_NOT_NULL);
    }

    @Test
    void testCreateLevelCompetenceUseCaseParam_valueParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.value(null)));
        assertThat(throwable).hasMessage("value: " + CREATE_LEVEL_COMPETENCE_VALUE_NOT_NULL);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.value(0)));
        assertThat(throwable).hasMessage("value: " + CREATE_LEVEL_COMPETENCE_VALUE_MIN);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.value(101)));
        assertThat(throwable).hasMessage("value: " + CREATE_LEVEL_COMPETENCE_VALUE_MAX);
    }

    @Test
    void testCreateLevelCompetenceUseCaseParam_currentUserParamViolatesConstraints_ErrorMessage() {
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
            .affectedLevelId(3L)
            .effectiveLevelId(2L)
            .value(60)
            .currentUserId(UUID.randomUUID());
    }
}
