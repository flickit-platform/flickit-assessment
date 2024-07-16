package org.flickit.assessment.core.adapter.out.email;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.config.AppSpecProperties;
import org.flickit.assessment.core.application.port.mail.SendFlickitInviteMailPort;

import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import static org.flickit.assessment.common.config.EmailConfig.EMAIL_SENDER_THREAD_EXECUTOR;
import static org.flickit.assessment.common.error.ErrorMessageKey.INVITE_SPACE_MEMBER_MAIL_SUBJECT;
import static org.flickit.assessment.common.error.ErrorMessageKey.INVITE_SPACE_MEMBER_MAIL_BODY;

@Slf4j
@Component("coreEmailAdapter")
@RequiredArgsConstructor
public class EmailAdapter implements
    SendFlickitInviteMailPort {

    private final JavaMailSender mailSender;
    private final AppSpecProperties appSpecProperties;
    private final MailProperties springMailProperties;

    @Override
    public void inviteToFlickit(String to) {
        String subject = MessageBundle.message(INVITE_SPACE_MEMBER_MAIL_SUBJECT , appSpecProperties.getName());
        String text =  MessageBundle.message(INVITE_SPACE_MEMBER_MAIL_BODY , appSpecProperties.getHost(), appSpecProperties.getName());
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
