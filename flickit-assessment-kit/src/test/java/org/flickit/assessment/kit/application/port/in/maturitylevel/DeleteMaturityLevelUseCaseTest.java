package org.flickit.assessment.kit.application.port.in.maturitylevel;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.DELETE_MATURITY_LEVEL_KIT_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.DELETE_MATURITY_LEVEL_MATURITY_LEVEL_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.*;

class DeleteMaturityLevelUseCaseTest {

    @Test
    void testDeleteMaturityLevelParam_maturityLevelIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class, () -> createParam(b -> b.maturityLevelId(null)));
        assertThat(throwable).hasMessage("maturityLevelId: " + DELETE_MATURITY_LEVEL_MATURITY_LEVEL_ID_NOT_NULL);
    }

    @Test
    void testDeleteMaturityLevelParam_kitIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class, () -> createParam(b -> b.kitId(null)));
        assertThat(throwable).hasMessage("kitId: " + DELETE_MATURITY_LEVEL_KIT_ID_NOT_NULL);
    }

    @Test
    void testDeleteMaturityLevelParam_currentUserIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class, () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<DeleteMaturityLevelUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private DeleteMaturityLevelUseCase.Param.ParamBuilder paramBuilder() {
        return DeleteMaturityLevelUseCase.Param.builder()
            .maturityLevelId(1L)
            .kitId(2L)
            .currentUserId(UUID.randomUUID());
    }
}
