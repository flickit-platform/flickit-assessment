package org.flickit.assessment.users.adapter.in.rest.spaceinvitee;

import java.util.UUID;

public record AcceptSpaceInvitationRequestDto(UUID userId, String email) {
}
