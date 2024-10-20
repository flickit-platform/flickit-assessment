package org.flickit.assessment.core.application.port.out.assessmentinvite;

import java.time.LocalDateTime;
import java.util.UUID;

public interface CreateAssessmentInvitePort {

    void persist(Param param);

    record Param(UUID assessmentId,
                 String email,
                 Integer roleId,
                 LocalDateTime expirationTime,
                 LocalDateTime creationTime,
                 UUID createdBy){
    }
}
