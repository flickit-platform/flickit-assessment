package org.flickit.assessment.common.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("org.flickit.assessment.common")
@EnableConfigurationProperties({AppSpecProperties.class, FileProperties.class, OpenAiProperties.class, AppAiProperties.class})
public class AssessmentCommonAutoConfig {
}
