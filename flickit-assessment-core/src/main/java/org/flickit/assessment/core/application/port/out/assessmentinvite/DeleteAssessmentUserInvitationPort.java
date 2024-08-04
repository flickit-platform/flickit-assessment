package org.flickit.assessment.core.application.port.out.assessmentinvite;

public interface DeleteAssessmentUserInvitationPort {

    void deleteAllByEmail(String email);
}
