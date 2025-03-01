package org.flickit.assessment.users.application.service.space.notification;

public record CreateSpaceNotificationPayload(UserModel userModel) {

    public record UserModel(String displayName, String email) {
    }
}
