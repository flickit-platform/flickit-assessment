package org.flickit.assessment.users.adapter.out.email.spaceuseraccess;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.config.AppSpecProperties;
import org.flickit.assessment.users.application.port.out.mail.SendExpertGroupInviteMailPort;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.SendInviteMailPort;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static org.flickit.assessment.common.config.EmailConfig.EMAIL_SENDER_THREAD_EXECUTOR;
import static org.flickit.assessment.users.common.MessageKey.INVITE_EXPERT_GROUP_MEMBER_MAIL_BODY;
import static org.flickit.assessment.users.common.MessageKey.INVITE_EXPERT_GROUP_MEMBER_MAIL_SUBJECT;

@Slf4j
@Component("spaceUserAccessEmailAdapter")
@RequiredArgsConstructor
public class EmailAdapter implements SendInviteMailPort {

    private final JavaMailSender mailSender;
    private final AppSpecProperties appSpecProperties;
    private final MailProperties springMailProperties;

    @Override
    @SneakyThrows
    @Async(EMAIL_SENDER_THREAD_EXECUTOR)
    @Retryable(retryFor = Exception.class, maxAttempts = 5, backoff = @Backoff(delay = 10000))
    public void sendInviteMail(String to) {
        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        String text =  MessageBundle.message(INVITE_EXPERT_GROUP_MEMBER_MAIL_BODY, appSpecProperties.getHost());
        helper.setTo(to);
        helper.setFrom(springMailProperties.getUsername());
        helper.setSubject(MessageBundle.message(INVITE_EXPERT_GROUP_MEMBER_MAIL_SUBJECT));
        helper.setText(text, true);

        log.debug("Sending 'invite email to [{}]", to);

        mailSender.send(message);
    }
}
