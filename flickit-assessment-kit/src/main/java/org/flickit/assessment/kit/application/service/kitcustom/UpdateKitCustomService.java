package org.flickit.assessment.kit.application.service.kitcustom;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import org.flickit.assessment.kit.application.port.in.kitcustom.UpdateKitCustomUseCase;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateKitCustomService implements UpdateKitCustomUseCase {

    @Override
    public void updateKitCustom(Param param) {

    }

    private Param createParam(Consumer<Param.ParamBuilder> changer) {
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

    private UpdateKitCustomUseCase.Param.KitCustomData createCustomDataParam(Consumer<Param.KitCustomData.KitCustomDataBuilder> changer) {
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
