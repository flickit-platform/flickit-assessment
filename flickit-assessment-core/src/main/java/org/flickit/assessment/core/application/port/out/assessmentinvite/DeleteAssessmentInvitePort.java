package org.flickit.assessment.core.application.port.out.assessmentinvite;

import java.util.UUID;

public interface DeleteAssessmentInvitePort {

    void deleteById(UUID id);
}
