package org.flickit.assessment.kit.application.port.in.levelcompetence;

import jakarta.validation.ConstraintViolationException;
import org.flickit.assessment.kit.application.port.in.levelcompetence.GetLevelCompetencesUseCase.Param;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_LEVEL_COMPETENCES_KIT_VERSION_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GetLevelCompetencesUseCaseParamTest {

    @Test
    void testGetLevelCompetencesUseCaseParam_kitVersionIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.kitVersionId(null)));
        assertThat(throwable).hasMessage("kitVersionId: " + GET_LEVEL_COMPETENCES_KIT_VERSION_ID_NOT_NULL);
    }

    @Test
    void testGetLevelCompetencesUseCaseParam_currentUserParamViolatesConstraints_ErrorMessage() {
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
            .currentUserId(UUID.randomUUID());
    }
}