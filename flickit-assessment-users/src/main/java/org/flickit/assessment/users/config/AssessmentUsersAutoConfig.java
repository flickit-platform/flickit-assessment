package org.flickit.assessment.users.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("org.flickit.assessment.users")
@EnableConfigurationProperties(DslParserRestProperties.class)
public class AssessmentUsersAutoConfig {
}
