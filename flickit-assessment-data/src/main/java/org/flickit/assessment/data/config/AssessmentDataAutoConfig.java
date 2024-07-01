package org.flickit.assessment.data.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
@ComponentScan("org.flickit.assessment.data")
public class AssessmentDataAutoConfig {
}
