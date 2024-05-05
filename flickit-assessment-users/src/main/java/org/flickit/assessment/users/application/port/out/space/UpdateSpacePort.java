package org.flickit.assessment.users.application.port.out.space;

import java.time.LocalDateTime;
import java.util.UUID;

public interface UpdateSpacePort {

    void updateSpace(long id, LocalDateTime currentTime, UUID updatedBy);
}
