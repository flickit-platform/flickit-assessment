package org.flickit.assessment.common.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties("app.notif-sender")
public class NotificationSenderProperties {

    private TaskExecutorProps executor = new TaskExecutorProps();

    @Valid
    private NovuProps novu = new NovuProps();

    @Getter
    @Setter
    public static class NovuProps {

        @NotBlank
        private String apiKey;

        @NotBlank
        private String baseUrl;

        @NotBlank
        private String workflowGroupName;

        private String euBaseUrl;
    }
}
