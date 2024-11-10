package org.flickit.assessment.scenario.fixture.request;

import lombok.experimental.UtilityClass;
import org.flickit.assessment.users.adapter.in.rest.space.CreateSpaceRequestDto;

import java.util.function.Consumer;

@UtilityClass
public class CreateSpaceRequestDtoMother {

    private static int index = 0;

    public static CreateSpaceRequestDto createSpaceRequestDto() {
        return builder().build();
    }

    public static CreateSpaceRequestDto createSpaceRequestDto(Consumer<CreateSpaceRequestDto.CreateSpaceRequestDtoBuilder> changer) {
        var builder = builder();
        changer.accept(builder);
        return builder.build();
    }

    private static CreateSpaceRequestDto.CreateSpaceRequestDtoBuilder builder() {
        index++;
        return CreateSpaceRequestDto.builder()
            .title("My Space " + index);
    }
}
