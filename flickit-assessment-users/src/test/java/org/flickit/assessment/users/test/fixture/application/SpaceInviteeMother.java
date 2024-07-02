package org.flickit.assessment.users.test.fixture.application;

import org.flickit.assessment.users.application.domain.SpaceInvitee;

import java.time.LocalDateTime;
import java.util.UUID;

public class SpaceInviteeMother {

    public static SpaceInvitee createSpaceInvitee(long spaceId, String email) {
        return new SpaceInvitee(
            UUID.randomUUID(),
            email,
            spaceId,
            UUID.randomUUID(),
            LocalDateTime.now(),
            LocalDateTime.now()
        );
    }

    public static SpaceInvitee expiredInvitee(long spaceId, String email) {
        return new SpaceInvitee(
            UUID.randomUUID(),
            email,
            spaceId,
            UUID.randomUUID(),
            LocalDateTime.now(),
            LocalDateTime.now().minusDays(1)
        );
    }

    public static SpaceInvitee notExpiredInvitee(long spaceId, String email) {
        return new SpaceInvitee(
            UUID.randomUUID(),
            email,
            spaceId,
            UUID.randomUUID(),
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(1)
        );
    }
}
