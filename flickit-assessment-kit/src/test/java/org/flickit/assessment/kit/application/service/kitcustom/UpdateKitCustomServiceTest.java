package org.flickit.assessment.kit.application.service.kitcustom;

import org.flickit.assessment.kit.application.port.in.kitcustom.UpdateKitCustomUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

@ExtendWith(SpringExtension.class)
class UpdateKitCustomServiceTest {

    @Test
    void testUpdateKitCustom_ki_su() {
    }

    private UpdateKitCustomUseCase.Param createParam(Consumer<UpdateKitCustomUseCase.Param.ParamBuilder> changer) {
        var param = paramBuilder();
        changer.accept(param);
        return param.build();
    }

    private UpdateKitCustomUseCase.Param.ParamBuilder paramBuilder() {
        return UpdateKitCustomUseCase.Param.builder()
            .id(12L)
            .kitId(1L)
            .title("title")
            .customData(createCustomDataParam(UpdateKitCustomUseCase.Param.KitCustomData.KitCustomDataBuilder::build))
            .currentUserId(UUID.randomUUID());
    }

    private UpdateKitCustomUseCase.Param.KitCustomData createCustomDataParam(Consumer<UpdateKitCustomUseCase.Param.KitCustomData.KitCustomDataBuilder> changer) {
        var param = KitCustomDataBuilder();
        changer.accept(param);
        return param.build();
    }

    private UpdateKitCustomUseCase.Param.KitCustomData.KitCustomDataBuilder KitCustomDataBuilder() {
        var customSubject = new UpdateKitCustomUseCase.Param.KitCustomData.CustomSubject(1L, 1);
        var customAttribute = new UpdateKitCustomUseCase.Param.KitCustomData.CustomAttribute(1L, 1);
        return UpdateKitCustomUseCase.Param.KitCustomData.builder()
            .customSubjects(List.of(customSubject))
            .customAttributes(List.of(customAttribute));
    }
}
