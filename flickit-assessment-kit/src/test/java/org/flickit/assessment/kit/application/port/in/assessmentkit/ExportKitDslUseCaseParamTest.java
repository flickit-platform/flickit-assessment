package org.flickit.assessment.kit.application.port.in.assessmentkit;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.EXPORT_KIT_DSL_KIT_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.*;

class ExportKitDslUseCaseParamTest {

    @Test
    void testExportKitDslUseCaseParam_KitIdParamIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.kitId(null)));
        assertThat(throwable).hasMessage("kitId: " + EXPORT_KIT_DSL_KIT_ID_NOT_NULL);
    }

    @Test
    void testExportKitDslUseCaseParam_currentUserIdParamIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<ExportKitDslUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private ExportKitDslUseCase.Param.ParamBuilder paramBuilder() {
        return ExportKitDslUseCase.Param.builder()
            .kitId(123L)
            .currentUserId(UUID.randomUUID());
    }
}
