package org.flickit.assessment.users.adapter.in.rest.expertgroupaccess;

import lombok.Builder;

import java.util.UUID;

@Builder
public record InviteExpertGroupMemberRequestDto(UUID userId) {
}
