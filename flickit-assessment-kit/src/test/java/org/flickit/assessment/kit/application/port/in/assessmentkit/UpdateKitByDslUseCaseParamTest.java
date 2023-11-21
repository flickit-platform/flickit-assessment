package org.flickit.assessment.kit.application.port.in.assessmentkit;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.kit.common.ErrorMessageKey.UPDATE_KIT_BY_DSL_DSL_CONTENT_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.UPDATE_KIT_BY_DSL_KIT_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class UpdateKitByDslUseCaseParamTest {

    @Test
    void testMaturityLevelUpdateKitPersister_kitIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateKitByDslUseCase.Param(null, "dslContent"));
        assertThat(throwable).hasMessage("kitId: " + UPDATE_KIT_BY_DSL_KIT_ID_NOT_NULL);
    }

    @Test
    void testMaturityLevelUpdateKitPersister_dslContentIdBlank_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateKitByDslUseCase.Param(1L, ""));
        assertThat(throwable).hasMessage("dslContent: " + UPDATE_KIT_BY_DSL_DSL_CONTENT_NOT_NULL);
    }

}
