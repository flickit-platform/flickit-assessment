package org.flickit.assessment.scenario.fixture.request;

import lombok.experimental.UtilityClass;
import org.flickit.assessment.users.adapter.in.rest.expertgroup.UpdateExpertGroupRequestDto;

import java.util.function.Consumer;

@UtilityClass
public class UpdateExpertGroupRequestDtoMother {

    public static UpdateExpertGroupRequestDto updateExpertGroupRequestDto() {
        return builder().build();
    }

    public static UpdateExpertGroupRequestDto updateExpertGroupRequestDto(Consumer<UpdateExpertGroupRequestDto.UpdateExpertGroupRequestDtoBuilder> changer) {
        var builder = builder();
        changer.accept(builder);
        return builder.build();
    }

    private static UpdateExpertGroupRequestDto.UpdateExpertGroupRequestDtoBuilder builder() {
        return UpdateExpertGroupRequestDto.builder()
            .title("New ExpertGroup")
            .bio("New Bio")
            .about("New About")
            .website("https://newFlickit.org");
    }
}
