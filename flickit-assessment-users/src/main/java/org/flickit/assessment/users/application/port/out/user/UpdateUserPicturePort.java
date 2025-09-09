package org.flickit.assessment.users.application.port.out.user;

import java.time.LocalDateTime;
import java.util.UUID;

public interface UpdateUserPicturePort {

    void updatePicture(UUID userId, String picture, LocalDateTime lastModificationTime);
}
