package org.flickit.assessment.users.application.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class SpaceInvitee {

    private final UUID id;
    private final String email;
    private final long spaceId;
    private final UUID inviterId;
    private final LocalDateTime inviteTime;
    private final LocalDateTime expirationTime;

    public boolean isNotExpired() {
        return expirationTime.isAfter(LocalDateTime.now());
    }
}
