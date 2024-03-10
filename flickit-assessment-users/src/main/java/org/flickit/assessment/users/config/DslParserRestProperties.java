package org.flickit.assessment.users.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("app.dsl-parser")
public class DslParserRestProperties {

    private String url;
}
