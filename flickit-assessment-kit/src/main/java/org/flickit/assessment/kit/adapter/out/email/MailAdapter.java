package org.flickit.assessment.kit.adapter.out.email;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.flickit.assessment.kit.application.port.out.mail.SendExpertGroupInvitationMailPort;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@EnableRetry
@AllArgsConstructor
public class MailAdapter implements
    SendExpertGroupInvitationMailPort {

    private JavaMailSender mailSender;


    @SneakyThrows
    @Retryable(retryFor = Exception.class, maxAttempts = 10, backoff = @Backoff(delay = 10000))
    @Override
    public void sendInviteExpertGroupMemberEmail(String to, UUID inviteToken) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("invite to join");
        message.setText("/expert-groups/invite/confirm/{invite_token}/"+inviteToken.toString());
        mailSender.send(message);
    }
}
