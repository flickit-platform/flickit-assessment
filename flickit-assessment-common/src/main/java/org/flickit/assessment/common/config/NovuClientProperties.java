package org.flickit.assessment.common.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties("app.novu")
public class NovuClientProperties {

    @NotBlank
    private String apiKey;

    @NotBlank
    private String workflowGroupName;
}
