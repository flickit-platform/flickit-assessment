package org.flickit.assessment.advice.application.service.adviceitem;

import org.flickit.assessment.advice.application.port.in.adviceitem.UpdateAdviceItemUseCase;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.function.Consumer;

@ExtendWith(MockitoExtension.class)
class UpdateAdviceItemServiceTest {


    private UpdateAdviceItemUseCase.Param createParam(Consumer<UpdateAdviceItemUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private UpdateAdviceItemUseCase.Param.ParamBuilder paramBuilder() {
        return UpdateAdviceItemUseCase.Param.builder()
            .adviceItemId(UUID.randomUUID())
            .assessmentId(UUID.randomUUID())
            .title("title")
            .description("description")
            .cost("LOW")
            .impact("MEDIUM")
            .priority("HIGH")
            .currentUserId(UUID.randomUUID());
    }
}
