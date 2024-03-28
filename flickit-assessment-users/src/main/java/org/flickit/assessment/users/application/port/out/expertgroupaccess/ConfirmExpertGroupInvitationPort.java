package org.flickit.assessment.users.application.port.out.expertgroupaccess;

import java.util.UUID;

public interface ConfirmExpertGroupInvitationPort {

    void confirmInvitation(UUID inviteToken);
}
