package org.flickit.assessment.core.application.port.out.assessmentinvitee;

import java.util.UUID;

public interface DeleteAssessmentInviteePort {

    void deleteById(UUID id);
}
