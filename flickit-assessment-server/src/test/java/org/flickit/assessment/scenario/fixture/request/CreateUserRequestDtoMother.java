package org.flickit.assessment.scenario.fixture.request;

import lombok.experimental.UtilityClass;
import org.flickit.assessment.users.adapter.in.rest.user.CreateUserRequestDto;

import java.util.UUID;
import java.util.function.Consumer;

@UtilityClass
public class CreateUserRequestDtoMother {

    private static int index = 0;

    public static CreateUserRequestDto createUserRequestDto() {
        return builder().build();
    }

    public static CreateUserRequestDto createUserRequestDto(Consumer<CreateUserRequestDto.CreateUserRequestDtoBuilder> changer) {
        var builder = builder();
        changer.accept(builder);
        return builder.build();
    }

    private static CreateUserRequestDto.CreateUserRequestDtoBuilder builder() {
        index++;
        return CreateUserRequestDto.builder()
            .id(UUID.randomUUID())
            .displayName("Jane Doe " + index)
            .email("jane-doe-%s@gmail.com".formatted(index));
    }

}
