package org.flickit.assessment.kit.adapter.out.email;

import lombok.AllArgsConstructor;
import org.flickit.assessment.kit.application.port.out.mail.SendExpertGroupInvitationMailPort;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@AllArgsConstructor
public class MailAdapter implements
    SendExpertGroupInvitationMailPort {

    private JavaMailSender mailSender;

    @Override
    public void sendInviteExpertGroupMemberEmail(String to, UUID inviteToken) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("subject");
        message.setText("body" + inviteToken.toString());
        mailSender.send(message);
    }
}
