package org.flickit.assessment.scenario.fixture.request;

import lombok.experimental.UtilityClass;
import org.flickit.assessment.kit.adapter.in.rest.assessmentkit.CreateKitByDslRequestDto;
import org.flickit.assessment.kit.adapter.in.rest.assessmentkit.CreateKitByDslRequestDto.CreateKitByDslRequestDtoBuilder;

import java.util.List;
import java.util.function.Consumer;

@UtilityClass
public class CreateKitByDslRequestDtoMother {

    private static int index = 0;

    public static CreateKitByDslRequestDto createKitByDslRequestDto() {
        return builder().build();
    }

    public static CreateKitByDslRequestDto createKitByDslRequestDto(Consumer<CreateKitByDslRequestDtoBuilder> changer) {
        var builder = builder();
        changer.accept(builder);
        return builder.build();
    }

    private static CreateKitByDslRequestDtoBuilder builder() {
        index++;
        return CreateKitByDslRequestDto.builder()
            .title("Assessment kit " + index)
            .summary("kit summary " + index)
            .about("about kit " + index)
            .lang("en")
            .isPrivate(false)
            .kitDslId(6350L + index)
            .expertGroupId(1560L + index)
            .tagIds(List.of(1L, 2L));
    }
}
