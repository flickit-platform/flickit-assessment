package org.flickit.assessment.users.adapter.out.email;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.config.AppSpecProperties;
import org.flickit.assessment.users.application.port.out.mail.SendExpertGroupInviteMailPort;
import org.flickit.assessment.users.application.port.out.mail.SendFlickitInviteMailPort;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static org.flickit.assessment.common.config.EmailConfig.EMAIL_SENDER_THREAD_EXECUTOR;
import static org.flickit.assessment.users.common.MessageKey.*;
import static org.flickit.assessment.common.error.ErrorMessageKey.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailAdapter implements
    SendExpertGroupInviteMailPort,
    SendFlickitInviteMailPort {

    private final JavaMailSender mailSender;
    private final AppSpecProperties appSpecProperties;
    private final MailProperties springMailProperties;

    @Override
    public void inviteToExpertGroup(String to, long expertGroupId, UUID inviteToken) {
        String subject = MessageBundle.message(INVITE_EXPERT_GROUP_MEMBER_MAIL_SUBJECT);
        String text = createText(expertGroupId, inviteToken);
        log.debug("Sending 'invite to expertGroup [{}]' email to [{}]", expertGroupId, to);
        sendMail(to, subject, text);
    }

    private String createText(long expertGroupId, UUID inviteToken) {
        String inviteUrl = String.join("/", appSpecProperties.getHost(), appSpecProperties.getExpertGroupInviteUrlPath(),
            String.valueOf(expertGroupId), inviteToken.toString());
        return MessageBundle.message(INVITE_EXPERT_GROUP_MEMBER_MAIL_BODY, inviteUrl, appSpecProperties.getName());
    }

    @Override
    public void inviteToFlickit(String to) {
        String subject = MessageBundle.message(INVITE_TO_REGISTER_MAIL_SUBJECT, appSpecProperties.getName());
        String text =  MessageBundle.message(INVITE_TO_REGISTER_MAIL_BODY, appSpecProperties.getHost(), appSpecProperties.getName());
        log.debug("Sending invite email to [{}]", to);
        sendMail(to, subject, text);
    }

    @SneakyThrows
    @Async(EMAIL_SENDER_THREAD_EXECUTOR)
    @Retryable(retryFor = Exception.class, maxAttempts = 5, backoff = @Backoff(delay = 10000))
    private void sendMail(String to, String subject, String text){
        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setFrom(getFrom());
        helper.setSubject(subject);
        helper.setText(text, true);

        mailSender.send(message);
    }

    private String getFrom() {
        return String.format("%s <%s>", appSpecProperties.getEmail().getFromDisplayName(), springMailProperties.getUsername());
    }
}
