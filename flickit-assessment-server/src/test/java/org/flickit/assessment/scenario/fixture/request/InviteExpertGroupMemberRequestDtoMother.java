package org.flickit.assessment.scenario.fixture.request;

import lombok.experimental.UtilityClass;
import org.flickit.assessment.users.adapter.in.rest.expertgroupaccess.InviteExpertGroupMemberRequestDto;

import java.util.UUID;
import java.util.function.Consumer;

@UtilityClass
public class InviteExpertGroupMemberRequestDtoMother {

    public static InviteExpertGroupMemberRequestDto inviteMemberRequestDto() {
        return builder().build();
    }

    public static InviteExpertGroupMemberRequestDto inviteMemberRequestDto(Consumer<InviteExpertGroupMemberRequestDto.InviteExpertGroupMemberRequestDtoBuilder> changer) {
        var builder = builder();
        changer.accept(builder);
        return builder.build();
    }

    private static InviteExpertGroupMemberRequestDto.InviteExpertGroupMemberRequestDtoBuilder builder() {
        return InviteExpertGroupMemberRequestDto.builder()
            .userId(UUID.randomUUID());
    }
}
