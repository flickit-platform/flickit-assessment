package org.flickit.assessment.core.test.fixture.application;

import org.flickit.assessment.core.application.domain.AssessmentInvite;
import org.flickit.assessment.core.application.domain.AssessmentUserRole;

import java.time.LocalDateTime;
import java.util.UUID;

public class AssessmentInviteMother {

    public static AssessmentInvite expiredAssessmentInvite(String email) {
        return new AssessmentInvite(
            UUID.randomUUID(),
            UUID.randomUUID(),
            email,
            AssessmentUserRole.valueOfById(2),
            LocalDateTime.now().minusDays(2),
            LocalDateTime.now(),
            UUID.randomUUID()
        );
    }

    public static AssessmentInvite notExpiredAssessmentInvite(String email) {
        return new AssessmentInvite(
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
