package org.flickit.assessment.users.adapter.in.rest.user;

import java.util.UUID;

public record CreateUserRequestDto(UUID id, String email, String displayName) {
}
