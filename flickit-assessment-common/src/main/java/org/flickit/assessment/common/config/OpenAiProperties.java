package org.flickit.assessment.common.config;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties("app.open-ai")
@RequiredArgsConstructor
public class OpenAiProperties {

    @NotNull
    private String apiUrl;

    @NotNull
    private String apiKey;

    @NotNull
    private String role;

    @NotNull
    private String model;

    @NotNull
    private String prompt = "As a software quality assessor, I evaluate the :attribute maturity of a system. " +
        "In the uploaded Excel file, there are multiple-choice questions regarding the tools that the software development team should use to enhance software security. " +
        "The Excel columns include the question, a hint for the question, the weight of the question in calculating the overall attribute score, and the actual score achieved by the software being assessed. " +
        "Please generate an executive summary highlighting the main strengths and weaknesses in less than 100 words. " +
        "Avoid mentioning the scores of individual questions. Use polite and considerate language, avoiding any derogatory terms.";

    public String createPrompt(String attribute) {
        return prompt.replace(":attribute", attribute);
    }
}
