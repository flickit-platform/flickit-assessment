package org.flickit.assessment.common.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties("spring.ai.openai")
@RequiredArgsConstructor
public class OpenAiProperties {

    @NotBlank
    private String apiKey;

    @NestedConfigurationProperty
    private DefaultChatOptions chatOptions;
}
