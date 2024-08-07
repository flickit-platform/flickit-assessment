package org.flickit.assessment.users.application.port.out.user;

import java.util.UUID;

public interface UpdateUserPicturePort {

    void updatePicture(UUID userId, String picture);
}
