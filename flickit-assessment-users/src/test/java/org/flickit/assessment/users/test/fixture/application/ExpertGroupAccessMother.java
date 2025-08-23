package org.flickit.assessment.users.test.fixture.application;

import org.flickit.assessment.users.application.domain.ExpertGroupAccess;
import org.flickit.assessment.users.application.domain.ExpertGroupAccessStatus;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.flickit.assessment.users.application.domain.ExpertGroupAccessStatus.PENDING;

public class ExpertGroupAccessMother {

    public static ExpertGroupAccess pendingAccess(UUID inviteToken) {
        return new ExpertGroupAccess(
            LocalDateTime.now().plusDays(10),
            inviteToken,
            PENDING);
    }

    public static ExpertGroupAccess expiredPendingAccess(UUID inviteToken) {
        return new ExpertGroupAccess(
            LocalDateTime.now().minusDays(1),
            inviteToken,
            PENDING);
    }

    public static ExpertGroupAccess activeAccess() {
        return new ExpertGroupAccess(
            null,
            null,
            ExpertGroupAccessStatus.ACTIVE
        );
    }
}
