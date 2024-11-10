package org.flickit.assessment.scenario.fixture.request;

import lombok.experimental.UtilityClass;
import org.flickit.assessment.users.adapter.in.rest.spaceuseraccess.AddSpaceMemberRequestDto;

import java.util.function.Consumer;

@UtilityClass
public class AddSpaceMemberRequestDtoMother {

    private static int index = 0;

    public static AddSpaceMemberRequestDto addSpaceMemberRequestDto() {
        return builder().build();
    }

    public static AddSpaceMemberRequestDto addSpaceMemberRequestDto(Consumer<AddSpaceMemberRequestDto.AddSpaceMemberRequestDtoBuilder> changer) {
        var builder = builder();
        changer.accept(builder);
        return builder.build();
    }

    private static AddSpaceMemberRequestDto.AddSpaceMemberRequestDtoBuilder builder() {
        index++;
        return AddSpaceMemberRequestDto.builder()
            .email("test" + index + "@test.test");
    }
}
