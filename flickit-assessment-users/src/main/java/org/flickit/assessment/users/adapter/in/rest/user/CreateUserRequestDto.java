package org.flickit.assessment.users.adapter.in.rest.user;

import lombok.Builder;

import java.util.UUID;

@Builder
public record CreateUserRequestDto(UUID id, String email, String displayName) {
}
