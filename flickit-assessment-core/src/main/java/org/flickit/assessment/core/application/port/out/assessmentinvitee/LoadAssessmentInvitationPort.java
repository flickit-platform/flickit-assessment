package org.flickit.assessment.core.application.port.out.assessmentinvitee;

import org.flickit.assessment.core.application.domain.AssessmentInvitee;

import java.util.Optional;
import java.util.UUID;

public interface LoadAssessmentInvitationPort {

    Optional<AssessmentInvitee> load(UUID invitationId);
}
