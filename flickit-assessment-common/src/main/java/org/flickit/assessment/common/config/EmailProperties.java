package org.flickit.assessment.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties("app.mail")
public class EmailProperties {

    private TaskExecutorProps executor = new TaskExecutorProps();
}
