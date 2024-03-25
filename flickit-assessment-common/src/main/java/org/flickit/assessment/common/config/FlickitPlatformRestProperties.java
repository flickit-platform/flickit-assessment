package org.flickit.assessment.common.config;

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
    private String getSubjectsUrl = "/api/internal/v1/assessment-kits/%d/subjects";

    @NotBlank
    private String getQuestionsUrl = "/api/internal/v1/assessment-kits/%d/questions?page=%d";

    @NotBlank
    private String getQuestionsBySubjectUrl = "/api/internal/v1/subjects/%d/questions";

    @NotBlank
    private String getMaturityLevelsUrl = "/api/internal/v1/assessment-kits/%d/maturity-levels";

    @NotBlank
    private String getAnswerOptionsUrl = "/api/internal/v1/answer-options?ids=%s";

    private int getAnswerOptionIdsLimit = 100;

    @Valid
    private RestClientProps restClient = new RestClientProps();
}
