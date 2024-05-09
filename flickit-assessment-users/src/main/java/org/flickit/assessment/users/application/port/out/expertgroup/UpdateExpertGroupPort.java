package org.flickit.assessment.users.application.port.out.expertgroup;

import java.time.LocalDateTime;
import java.util.UUID;

public interface UpdateExpertGroupPort {

    void update(Param param);

    record Param(long id, String code, String title, String about, String bio, String website,
                 LocalDateTime lastModificationTime, UUID lastModifiedBy){}
}
