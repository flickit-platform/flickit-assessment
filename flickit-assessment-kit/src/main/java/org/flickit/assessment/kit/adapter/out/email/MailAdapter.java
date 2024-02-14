package org.flickit.assessment.kit.adapter.out.email;

import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.flickit.assessment.kit.application.port.out.mail.SendExpertGroupInvitationMailPort;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true); // Enable HTML content and multipart support

        helper.setTo(to);
        helper.setSubject("Invite to join");

        String htmlContent = "<p>Dear recipient,</p>"
            + "<p>You have been invited to join our expert group. Please click the following link to confirm your invitation:</p>"
            + "<p><a href=\"http://www.example.com/expert-groups/invite/confirm/" + inviteToken.toString() + "\">Confirm Invitation</a></p>";

        helper.setText(htmlContent, true);
        mailSender.send(message);
    }
}
