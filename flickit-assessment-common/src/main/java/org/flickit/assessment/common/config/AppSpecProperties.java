package org.flickit.assessment.common.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.Map;
import java.util.Set;

import static org.flickit.assessment.common.application.domain.kit.KitLanguage.EN;
import static org.flickit.assessment.common.application.domain.kit.KitLanguage.FA;

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

    @NotBlank
    private String assessmentReportUrlPath = "{0}/{1}/assessments/{2}/graphical-report";

    @NotBlank
    private String name = "Flickit";

    private String nameFa = "";

    private String nameDefault = "Flickit";

    private LocaleProps defaultLocaleProp = new LocaleProps("Flickit");

    private Map<String, LocaleProps> localeProps = Map.of(
        "en", new LocaleProps("Flickit"),
        "fa", new LocaleProps("فلیکیت")
    );

    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LocaleProps {

        String appName;
    }

    @NotBlank
    private String logo;

    @NotBlank
    private String favIcon;

    private String supportEmail;

    private Set<KitLanguage> supportedKitLanguages = Set.of(EN, FA);

    @Valid
    private Email email = new Email();

    @Valid
    private Space space = new Space();

    @Setter
    @Getter
    @ToString
    public static class Email {

        @NotBlank
        String fromDisplayName = "Flickit";

        @NotBlank
        String adminEmail;
    }

    @Setter
    @Getter
    @ToString
    public static class Space {

        int maxBasicSpaces = 1;
        int maxBasicSpaceAssessments = 2;
    }
}
