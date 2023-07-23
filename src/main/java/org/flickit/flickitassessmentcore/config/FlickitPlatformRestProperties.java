package org.flickit.flickitassessmentcore.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties("app.flickit-platform.rest")
public class FlickitPlatformRestProperties {

    @NotBlank
    private String baseUrl;

    @NotBlank
    private String getSubjectIdsUrl = "/api/internal/assessment-kit/%d/assessment-subjects/";

    @Valid
    private RestClientProps restClient = new RestClientProps();
}
