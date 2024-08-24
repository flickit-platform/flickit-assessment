package org.flickit.assessment.common.adapter.out.email;


import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.common.application.port.SendEmailPort;
import org.flickit.assessment.common.config.AppSpecProperties;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import static org.flickit.assessment.common.config.EmailConfig.EMAIL_SENDER_THREAD_EXECUTOR;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailAdapter implements SendEmailPort {

    private final JavaMailSender mailSender;
    private final AppSpecProperties appSpecProperties;
    private final MailProperties springMailProperties;

    @Override
    @SneakyThrows
    @Async(EMAIL_SENDER_THREAD_EXECUTOR)
    @Retryable(retryFor = Exception.class, maxAttempts = 5, backoff = @Backoff(delay = 10000))
    public void send(String sendTo, String subject, String body) {
        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(sendTo);
        helper.setFrom(getFrom());
        helper.setSubject(subject);
        helper.setText(body, true);

        mailSender.send(message);
    }

    private String getFrom() {
        return "%s <%s>".formatted(appSpecProperties.getEmail().getFromDisplayName(), springMailProperties.getUsername());
    }
}
