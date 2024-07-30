package org.flickit.assessment.core.application.port.out.assessmentinvitee;

import java.util.UUID;

public interface UpdateAssessmentInviteeRolePort {

    void updateRole(UUID inviteId, Integer roleId);
}
