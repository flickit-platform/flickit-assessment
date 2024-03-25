package org.flickit.assessment.users.application.port.out.mail;

import java.util.UUID;

public interface SendExpertGroupInviteMailPort {

    void sendInvite(String email, long expertGroupId, UUID inviteToken);
}
