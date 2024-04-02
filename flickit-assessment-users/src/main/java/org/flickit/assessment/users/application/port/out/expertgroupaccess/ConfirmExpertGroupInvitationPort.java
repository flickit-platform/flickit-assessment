package org.flickit.assessment.users.application.port.out.expertgroupaccess;

import java.util.UUID;

public interface ConfirmExpertGroupInvitationPort {

    void confirmInvitation(long expertGroupId, UUID userId);
}
