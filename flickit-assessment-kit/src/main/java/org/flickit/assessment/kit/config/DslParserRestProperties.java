package org.flickit.assessment.kit.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("app.dsl-parser")
public class DslParserRestProperties {

    private String url;
}
