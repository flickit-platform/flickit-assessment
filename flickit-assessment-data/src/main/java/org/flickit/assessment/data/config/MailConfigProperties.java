package org.flickit.assessment.data.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties("app.flickit-platform.rest")
public class MailConfigProperties {

    @NotBlank
    private String baseUrl;

    @NotBlank
    private String getInviteUrl = "account/expert-group-invitation";
}
