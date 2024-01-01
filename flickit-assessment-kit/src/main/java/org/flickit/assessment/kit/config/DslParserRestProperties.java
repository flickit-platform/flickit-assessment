package org.flickit.assessment.kit.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("dsl.parser")
public class DslParserRestProperties {

    private String url = "http://localhost:8080/extract";

//    private String url = "http://dsl:8080/extract/";
}
