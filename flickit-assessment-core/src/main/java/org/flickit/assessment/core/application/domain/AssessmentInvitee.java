package org.flickit.assessment.core.application.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class AssessmentInvitee {

    private final UUID id;
    private final String email;
    private final AssessmentUserRole role;
    private final LocalDateTime expirationTime;
    private final LocalDateTime creationTime;
    private final UUID createdById;
}
