package org.flickit.assessment.scenario.fixture.request;

import lombok.experimental.UtilityClass;
import org.flickit.assessment.users.adapter.in.rest.space.UpdateSpaceRequestDto;

import java.util.function.Consumer;

@UtilityClass
public class UpdateSpaceRequestDtoMother {

    public static UpdateSpaceRequestDto updateSpaceRequestDto() {
        return builder().build();
    }

    public static UpdateSpaceRequestDto updateSpaceRequestDto(Consumer<UpdateSpaceRequestDto.UpdateSpaceRequestDtoBuilder> changer) {
        var builder = builder();
        changer.accept(builder);
        return builder.build();
    }

    private static UpdateSpaceRequestDto.UpdateSpaceRequestDtoBuilder builder() {
        return UpdateSpaceRequestDto.builder()
                .title("new title");
    }
}
