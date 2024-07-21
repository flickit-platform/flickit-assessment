package org.flickit.assessment.core.application.port.out.assessmentinvitee;

public interface DeleteAssessmentUserInvitationPort {

    void deleteAssessmentUserInvitationsByEmail(String email);
}
