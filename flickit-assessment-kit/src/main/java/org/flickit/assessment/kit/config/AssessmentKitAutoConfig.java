package org.flickit.assessment.kit.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("org.flickit.assessment.kit")
@EnableConfigurationProperties(DslParserRestProperties.class)
public class AssessmentKitAutoConfig {
}
