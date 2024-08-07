package org.flickit.assessment.common.config;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.flickit.assessment.common.util.TemplateEvaluator;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.Map;

@Getter
@Setter
@Validated
@ConfigurationProperties("app.open-ai")
@RequiredArgsConstructor
public class OpenAiProperties {

    private boolean enabled = false;

    @NotNull
    private String apiUrl;

    @NotNull
    private String apiKey;

    private String role = "system";

    private String model = "gpt-4o-mini";

    private float temperature = 0.5F;

    private String prompt = "As a software quality assessor, I have evaluated the {{title}} maturity of a system. " +
        "We define {{title}} as {{description}} The uploaded Excel file contains multiple-choice questions used to assess {{title}}. " +
        "The Excel columns include the question, a hint, the weight of the question in calculating the overall score," +
        " and the actual score achieved by the software. Please generate an executive summary highlighting the main strengths and weaknesses in less than 100 words. " +
        "Use polite and considerate language, avoiding any derogatory terms, and do not mention the scores of individual questions.";

    public String createPrompt(String title, String description) {
        return TemplateEvaluator.evaluate(prompt, Map.of("title", title, "description", description));
    }
}
