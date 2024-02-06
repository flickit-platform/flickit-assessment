package org.flickit.assessment.kit.application.port.out.mail;

import java.util.UUID;

public interface SendExpertGroupInvitationMailPort {

    void sendEmail(String email, UUID inviteLink);
}
