package org.flickit.assessment.users.adapter.in.rest.user;

import java.util.UUID;

public record GetUsersMeResponseDto(UUID id, String displayName, String pictureLink) {
}
