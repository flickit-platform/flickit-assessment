package org.flickit.assessment.core.application.port.out.assessmentinvitee;

public interface DeleteAssessmentUserInvitationPort {

    void deleteAllByEmail(String email);
}
