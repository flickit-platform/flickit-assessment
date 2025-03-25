package org.flickit.assessment.scenario.fixture.request;

import lombok.experimental.UtilityClass;
import org.flickit.assessment.kit.adapter.in.rest.assessmentkit.GrantUserAccessToKitRequestDto;

import java.util.UUID;
import java.util.function.Consumer;

@UtilityClass
public class GrantUserAccessToKitRequestDtoMother {

    public static GrantUserAccessToKitRequestDto grantUserAccessToKitRequestDto() {
        return builder().build();
    }

    public static GrantUserAccessToKitRequestDto grantUserAccessToKitRequestDto(Consumer<GrantUserAccessToKitRequestDto.GrantUserAccessToKitRequestDtoBuilder> changer) {
        var builder = builder();
        changer.accept(builder);
        return builder.build();
    }

    private static GrantUserAccessToKitRequestDto.GrantUserAccessToKitRequestDtoBuilder builder() {
        return GrantUserAccessToKitRequestDto.builder()
            .userId(UUID.randomUUID());
    }
}
