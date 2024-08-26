package org.flickit.assessment.common.application.port.out;

public interface SendEmailPort {

    void send(String sendTo, String subject, String body);
}
