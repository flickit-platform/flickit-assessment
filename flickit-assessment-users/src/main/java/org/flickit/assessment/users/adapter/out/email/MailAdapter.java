package org.flickit.assessment.users.adapter.out.email;

import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.data.config.MailConfigProperties;
import org.flickit.assessment.users.application.port.out.mail.SendExpertGroupInviteMailPort;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static org.flickit.assessment.users.common.MessageKey.INVITE_EXPERT_GROUP_MEMBER_MAIL_BODY;
import static org.flickit.assessment.users.common.MessageKey.INVITE_EXPERT_GROUP_MEMBER_MAIL_SUBJECT;

@Component
@EnableRetry
@AllArgsConstructor
public class MailAdapter implements
        SendExpertGroupInviteMailPort {

    private final JavaMailSender mailSender;
    private final MailConfigProperties properties;

    @SneakyThrows
    @Retryable(retryFor = Exception.class, maxAttempts = 10, backoff = @Backoff(delay = 10000))
    @Override
    public void sendInvite(String to, long expertGroupId, UUID inviteToken) {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(to);
        helper.setSubject(MessageBundle.message(INVITE_EXPERT_GROUP_MEMBER_MAIL_SUBJECT));
        String htmlContent = MessageBundle.message(INVITE_EXPERT_GROUP_MEMBER_MAIL_BODY,
            properties.getBaseUrl(), properties.getGetInviteUrl(), expertGroupId, inviteToken.toString());

        helper.setText(htmlContent, true);
        mailSender.send(message);
    }
}
