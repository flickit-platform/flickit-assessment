package org.flickit.assessment.core.application.domain.notification;

public record GrantAssessmentUserRolePayLoad(Assessment assessment,
                                             User assigner,
                                             Role role) {
    public record Assessment(String title) {
    }

    public record User(String displayName) {
    }

    public record Role(String title) {
    }
}
