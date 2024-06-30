package org.flickit.assessment.users.application.port.out.space;

import java.time.LocalDateTime;
import java.util.UUID;

public interface UpdateSpacePort {

    void updateSpace(Param param);

    record Param(long id, String title, LocalDateTime lastModificationTime, UUID lastModifiedBy){
    }
}
