package org.flickit.assessment.core.application.domain.notification;

public record GrantAssessmentUserRolePayLoad(AssessmentModel assessment,
                                             AssignerModel assigner,
                                             RoleModel role) {
    public record AssessmentModel(String title) {
    }

    public record AssignerModel(String displayName) {
    }

    public record RoleModel(String title) {
    }
}
