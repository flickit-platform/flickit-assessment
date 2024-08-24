package org.flickit.assessment.common.application.port;

public interface SendEmailPort {

    void send(String sendTo, String subject, String body);
}
