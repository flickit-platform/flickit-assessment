package org.flickit.assessment.users.adapter.in.rest.spaceuseraccess;

import lombok.Builder;

@Builder
public record AddSpaceMemberRequestDto(String email) {
}
