package org.flickit.assessment.users.application.port.out.expertgroup;

import java.time.LocalDateTime;

public interface DeleteExpertGroupPort {

    void deleteById(long expertGroupId, LocalDateTime deletionTime);
}
