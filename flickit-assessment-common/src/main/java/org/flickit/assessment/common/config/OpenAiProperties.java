package org.flickit.assessment.common.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.validation.annotation.Validated;

import java.util.Map;

@Getter
@Setter
@Validated
@ConfigurationProperties("spring.ai.openai")
@RequiredArgsConstructor
public class OpenAiProperties {

    @NestedConfigurationProperty
    private DefaultChatOptions chatOptions;

    private String attributeAiInsightPrompt = """
        As a software quality assessor, I have evaluated the {title} maturity of a system.
        We define {title} as {description}. The uploaded Excel file contains multiple-choice questions used to assess {title}.
        The Excel columns include the question, a hint, the weight of the question in calculating the overall score,
         and the actual score achieved by the software. Please generate an executive summary highlighting the main strengths and weaknesses in less than 100 words.
        Use polite and considerate language, avoiding any derogatory terms, and do not mention the scores of individual questions.
        Here is the uploaded Excel file: {excelFile}.
        """;

    private String adviceAiNarrationPrompt = """
        As a software quality assessor, I have evaluated the system's maturity level. Below, I have listed the selected option and provided a recommended improvement option.
        Please generate advice in up to 10 concise bullet points, with a total character limit of 800 characters, including HTML tags. You may shorten the list if it enhances clarity.
        Ensure the advice is polite, clear, and constructive, avoiding mention of individual scores or derogatory language. Focus on actionable suggestions.
        Format the response in HTML with <li> tags for each bullet point, wrapped in a <ul> tag, and enclose the entire list within <p> tags. Avoid using newline characters (\\n) and the <HTML> tag.
        Provided advice items: {adviceListItems}.
        Provided attribute level targets: {attributeLevelTargets}.
        """;

    public Prompt createAttributeAiInsightPrompt(String title, String description, String excelFile) {
        var promptTemplate = new PromptTemplate(attributeAiInsightPrompt, Map.of("title", title, "description", description, "excelFile", excelFile));
        return new Prompt(promptTemplate.createMessage(), chatOptions);
    }

    public Prompt createAdviceAiNarrationPrompt(String adviceListItems, String attributeLevelTargets) {
        var promptTemplate = new PromptTemplate(adviceAiNarrationPrompt, Map.of("adviceListItems", adviceListItems, "attributeLevelTargets", attributeLevelTargets));
        return new Prompt(promptTemplate.createMessage(), chatOptions);
    }
}
