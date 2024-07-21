package org.flickit.assessment.core.test.fixture.application;

import org.flickit.assessment.core.application.domain.AssessmentInvitee;
import org.flickit.assessment.core.application.domain.AssessmentUserRole;

import java.time.LocalDateTime;
import java.util.UUID;

public class AssessmentInviteeMother {

    public static AssessmentInvitee expiredAssessmentInvitee(String email) {
        return new AssessmentInvitee(
            UUID.randomUUID(),
            UUID.randomUUID(),
            email,
            AssessmentUserRole.valueOfById(2),
            LocalDateTime.now().minusDays(2),
            LocalDateTime.now(),
            UUID.randomUUID()
        );
    }

    public static AssessmentInvitee notExpiredAssessmentInvitee(String email) {
        return new AssessmentInvitee(
            UUID.randomUUID(),
            UUID.randomUUID(),
            email,
            AssessmentUserRole.valueOfById(1),
            LocalDateTime.now().plusDays(2),
            LocalDateTime.now(),
            UUID.randomUUID()
        );
    }
}
