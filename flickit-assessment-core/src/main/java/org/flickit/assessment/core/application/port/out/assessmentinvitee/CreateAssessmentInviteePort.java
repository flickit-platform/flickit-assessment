package org.flickit.assessment.core.application.port.out.assessmentinvitee;

import java.time.LocalDateTime;
import java.util.UUID;

public interface CreateAssessmentInviteePort {

    void persist(Param param);

    record Param(UUID assessmentId,
                 String email,
                 Integer roleId,
                 LocalDateTime creationTime,
                 LocalDateTime expirationDate,
                 UUID createdBy){}
}
