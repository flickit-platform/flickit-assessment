package org.flickit.assessment.users.application.port.out.user;

import java.util.UUID;

public interface UpdateUserProfilePicturePort {

    void updatePicture(UUID userId, String picture);
}
