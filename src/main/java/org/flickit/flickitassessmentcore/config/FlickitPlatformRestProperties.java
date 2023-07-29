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
    private String getSubjectIdsUrl = "/api/internal/assessment-kits/%d/assessment-subjects/";

    @NotBlank
    private String getQuestionImpactByIdUrl = "/api/internal/question-impacts/%d/";

    @NotBlank
    private String getQuestionsUrl = "/api/internal/quality-attributes/%d/question/";

    @NotBlank
    private String getQualityAttributesUrl = "/api/internal/assessment-subjects/%d/quality-attributes/";

    @NotBlank
    private String getMaturityLevelsUrl = "/api/internal/assessment-kits/%d/maturity-levels/";

    @NotBlank
    private String getLevelCompetencesUrl = "/api/internal/maturity-levels/%d/level-competences/";

    @NotBlank
    private String getAnswerOptionImpactsUrl = "/api/internal/answer-options/%d/%d/option-values/";

    @Valid
    private RestClientProps restClient = new RestClientProps();
}
