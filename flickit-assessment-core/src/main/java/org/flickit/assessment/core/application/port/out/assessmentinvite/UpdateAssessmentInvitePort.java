package org.flickit.assessment.core.application.port.out.assessmentinvite;

import java.util.UUID;

public interface UpdateAssessmentInvitePort {

    void updateRole(UUID inviteId, Integer roleId);
}
