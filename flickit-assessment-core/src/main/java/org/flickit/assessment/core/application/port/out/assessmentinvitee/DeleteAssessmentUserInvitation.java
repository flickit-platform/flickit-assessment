package org.flickit.assessment.core.application.port.out.assessmentinvitee;

public interface DeleteAssessmentUserInvitation {

    void deleteAssessmentUserInvitationsByEmail(String email);
}
