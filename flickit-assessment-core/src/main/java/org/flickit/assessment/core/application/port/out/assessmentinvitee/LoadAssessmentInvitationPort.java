package org.flickit.assessment.core.application.port.out.assessmentinvitee;

import org.flickit.assessment.core.application.domain.AssessmentInvitee;

import java.util.UUID;

public interface LoadAssessmentInvitationPort {

    AssessmentInvitee loadById(UUID invitationId);
}
