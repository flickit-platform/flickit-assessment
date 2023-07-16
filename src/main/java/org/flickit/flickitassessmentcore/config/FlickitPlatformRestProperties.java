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

    @NotBlank
    private String getQuestionImpactsUrl = "/api/internal/questionimpact/%d/";

    @NotBlank
    private String getQuestionsUrl = "/api/internal/quality-attribute/%d/question/";

    @NotBlank
    private String getQualityAttributesUrl = "/api/internal/assessment-subject/%d/quality-attributes/";

    @NotBlank
    private String getMaturityLevelsUrl = "/api/internal/assessment-kit/%d/maturity-levels/";

    @NotBlank
    private String getLevelCompetencesUrl = "/api/internal/maturity-level/%d/level-competences/";

    @NotBlank
    private String getAnswerOptionImpactsUrl = "/api/internal/answer-template/%d/option-values/";

    @Valid
    private RestClientProps restClient = new RestClientProps();
}
