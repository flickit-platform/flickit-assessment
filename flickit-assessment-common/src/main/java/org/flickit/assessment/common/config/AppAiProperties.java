package org.flickit.assessment.common.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties("app.ai")
@RequiredArgsConstructor
public class AppAiProperties {

    private boolean enabled = false;

    private boolean saveAiInputFileEnabled = false;
}
