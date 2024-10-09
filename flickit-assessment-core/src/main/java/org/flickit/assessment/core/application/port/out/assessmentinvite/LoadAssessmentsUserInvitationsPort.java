package org.flickit.assessment.core.application.port.out.assessmentinvite;

import org.flickit.assessment.core.application.domain.AssessmentInvite;

import java.util.List;

public interface LoadAssessmentsUserInvitationsPort {

    List<AssessmentInvite> loadInvitations(String email);
}
