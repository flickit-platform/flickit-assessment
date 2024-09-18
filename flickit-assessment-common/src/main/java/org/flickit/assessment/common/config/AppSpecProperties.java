package org.flickit.assessment.common.config;

import jakarta.validation.Valid;
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
    private String domain;

    @NotBlank
    private String expertGroupInviteUrlPath = "account/expert-group-invitation";

    @NotBlank
    private String name = "Flickit";

    @NotBlank
    private String logo;

    @NotBlank
    private String favIcon;

    @Valid
    private Email email = new Email();

    @Setter
    @Getter
    @ToString
    public static class Email {

        @NotBlank
        String fromDisplayName = "Flickit";
    }
}
