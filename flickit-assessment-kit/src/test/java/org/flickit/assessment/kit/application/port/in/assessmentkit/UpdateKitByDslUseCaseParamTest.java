package org.flickit.assessment.kit.application.port.in.assessmentkit;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.kit.common.ErrorMessageKey.UPDATE_KIT_BY_DSL_KIT_DSL_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.UPDATE_KIT_BY_DSL_KIT_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class UpdateKitByDslUseCaseParamTest {

    @Test
    void testMaturityLevelUpdateKitPersister_kitIdIsNull_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateKitByDslUseCase.Param(null, 12L, currentUserId));
        assertThat(throwable).hasMessage("kitId: " + UPDATE_KIT_BY_DSL_KIT_ID_NOT_NULL);
    }

    @Test
    void testMaturityLevelUpdateKitPersister_kitDslIdIsNull_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateKitByDslUseCase.Param(1L, null, currentUserId));
        assertThat(throwable).hasMessage("kitDslId: " + UPDATE_KIT_BY_DSL_KIT_DSL_ID_NOT_NULL);
    }

}
