package org.flickit.assessment.core.application.port.out.assessmentinvitee;

import org.flickit.assessment.core.application.domain.AssessmentInvitee;

import java.util.List;

public interface LoadAssessmentsUserInvitationsPort {

    List<AssessmentInvitee> loadInvitations(String email);
}
