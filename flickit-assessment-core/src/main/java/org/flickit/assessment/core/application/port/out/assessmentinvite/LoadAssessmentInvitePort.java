package org.flickit.assessment.core.application.port.out.assessmentinvite;

import org.flickit.assessment.core.application.domain.AssessmentInvite;

import java.util.UUID;

public interface LoadAssessmentInvitePort {

    AssessmentInvite load(UUID id);
}
