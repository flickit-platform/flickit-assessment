package org.flickit.assessment.common.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Setter
@Getter
@ToString
@Validated
@ConfigurationProperties("app.spec")
public class AppSpecProperties {

    @NotBlank
    private String host;

    @NotBlank
    private String expertGroupInviteUrlPath = "account/expert-group-invitation";
}
