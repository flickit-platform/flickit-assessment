package org.flickit.assessment.users.application.port.out.mail;

import java.util.UUID;

public interface SendExpertGroupInvitationMailPort {

    void sendInviteExpertGroupMemberEmail(String email, UUID inviteLink);
}
