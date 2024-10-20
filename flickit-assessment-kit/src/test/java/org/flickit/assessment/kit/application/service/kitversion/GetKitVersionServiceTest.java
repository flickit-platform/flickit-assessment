package org.flickit.assessment.kit.application.service.kitversion;

import org.flickit.assessment.kit.application.port.in.kitversion.GetKitVersionUseCase;
import org.flickit.assessment.kit.application.port.in.kitversion.GetKitVersionUseCase.Param;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.function.Consumer;

@ExtendWith(MockitoExtension.class)
class GetKitVersionServiceTest {

    @Test
    void testGetKitVersionService_kitVersionIdNotExist_ShouldReturnResourceNotFoundException() {
        Param param = createParam(GetKitVersionUseCase.Param.ParamBuilder::build);

    }

    private Param createParam(Consumer<Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private Param.ParamBuilder paramBuilder() {
        return GetKitVersionUseCase.Param.builder()
            .kitVersionId(1L)
            .currentUserId(UUID.randomUUID());
    }
}
