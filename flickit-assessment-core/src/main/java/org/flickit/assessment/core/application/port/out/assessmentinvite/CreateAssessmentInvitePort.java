package org.flickit.assessment.core.application.port.out.assessmentinvite;

import org.flickit.assessment.core.application.domain.AssessmentUserRole;

import java.time.LocalDateTime;
import java.util.UUID;

public interface CreateAssessmentInvitePort {

    void persist(Param param);

    record Param(UUID assessmentId,
                 String email,
                 AssessmentUserRole role,
                 LocalDateTime expirationTime,
                 LocalDateTime creationTime,
                 UUID createdBy){
    }
}
