package org.flickit.assessment.scenario.fixture.request;

import lombok.experimental.UtilityClass;
import org.flickit.assessment.users.adapter.in.rest.expertgroup.CreateExpertGroupRequestDto;

import java.util.function.Consumer;

import static org.flickit.assessment.scenario.fixture.request.MultiPartFileMother.picture;

@UtilityClass
public class CreateExpertGroupRequestDtoMother {

    private static int index = 0;

    public static CreateExpertGroupRequestDto createExpertGroupRequestDto() {
        return builder().build();
    }

    public static CreateExpertGroupRequestDto createExpertGroupRequestDto(Consumer<CreateExpertGroupRequestDto.CreateExpertGroupRequestDtoBuilder> changer) {
        var builder = builder();
        changer.accept(builder);
        return builder.build();
    }

    private static CreateExpertGroupRequestDto.CreateExpertGroupRequestDtoBuilder builder() {
        index++;
        return CreateExpertGroupRequestDto.builder()
            .title("My ExpertGroup " + index)
            .bio("Bio " + index)
            .about("About " + index)
            .website("https://flickit.org")
            .picture(picture());
    }
}
