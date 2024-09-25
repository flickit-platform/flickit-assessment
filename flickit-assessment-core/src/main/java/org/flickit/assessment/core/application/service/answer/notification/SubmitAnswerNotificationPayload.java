package org.flickit.assessment.core.application.service.answer.notification;

import org.flickit.assessment.common.application.domain.notification.NotificationPayload;
import org.flickit.assessment.core.application.domain.Assessment;
import org.flickit.assessment.core.application.domain.User;

import java.util.UUID;

public record SubmitAnswerNotificationPayload(AssessmentModel assessment,
                                              UserModel assessor) implements NotificationPayload {

    public record AssessmentModel(UUID id, String title, Long spaceId) {

        public AssessmentModel(Assessment assessment) {
            this(assessment.getId(), assessment.getTitle(), assessment.getSpace().getId());
        }
    }

    public record UserModel(UUID id, String displayName) {

        public UserModel(User user) {
            this(user.getId(), user.getDisplayName());
        }
    }
}
