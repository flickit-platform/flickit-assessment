package org.flickit.assessment.users.application.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class ExpertGroupAccess {

    private final LocalDateTime inviteExpirationDate;

    private final UUID inviteToken;

    private final ExpertGroupAccessStatus status;
}
