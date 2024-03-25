package org.flickit.assessment.data.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties("spring.mail")
public class MailConfigProperties {

    @NotBlank
    private String baseUrl;

    @NotBlank
    private String getInviteUrl = "account/expert-group-invitation";
}
