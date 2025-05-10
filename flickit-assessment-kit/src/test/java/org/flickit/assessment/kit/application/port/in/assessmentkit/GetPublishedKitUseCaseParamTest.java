package org.flickit.assessment.kit.application.port.in.assessmentkit;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_PUBLISHED_KIT_KIT_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GetPublishedKitUseCaseParamTest {

    @Test
    void testGetPublishedKitUseCaseParam_kitIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.kitId(null)));
        assertThat(throwable).hasMessage("kitId: " + GET_PUBLISHED_KIT_KIT_ID_NOT_NULL);
    }

    @Test
    void testGetPublishedKitUseCaseParam_currentUserIdParamIsNull_ErrorMessage() {
        assertDoesNotThrow(() -> createParam(b -> b.currentUserId(null)));
    }

    private void createParam(Consumer<GetPublishedKitUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private GetPublishedKitUseCase.Param.ParamBuilder paramBuilder() {
        return GetPublishedKitUseCase.Param.builder()
            .kitId(0L)
            .currentUserId(UUID.randomUUID());
    }
}
