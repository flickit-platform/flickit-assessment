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

    private String aiAdviceNarrationPrompt = """
        For an assessment, titled {assessmentTitle}, an assessment platform has evaluated a software product by analyzing responses to various questions, each influencing specific quality attributes.
        The user has set maturity level targets for each attribute, and the platform has provided actionable advice items, highlighting which questions should be improved to achieve these targets.
        The advice includes the current status (selected option) and the goal status for each relevant question.
        Task: Based on the provided advice items, generate a clear narrative in up to 10 concise bullet points formatted with HTML tags (this could be 2, 3, or more, depending on the advice).
        Ensure that the advice is polite, constructive, and focused on actionable improvements, tailored for an expert software assessor.
        Avoid references to individual scores or negative phrasing. Keep the tone professional and supportive.
        Before the bullets, write a brief paragraph mentioning of the attributes and their target levels in no more than two sentences and put it in paragraph HTML tag.
        Attribute Targets: {attributeLevelTargets}

        Advice Items: {adviceListItems}

        Make sure the overall response size, including HTML tags, remains under 1000 characters and excludes any markdown.
        """;

    public Prompt createAttributeAiInsightPrompt(String title, String description, String excelFile) {
        var promptTemplate = new PromptTemplate(attributeAiInsightPrompt, Map.of("title", title, "description", description, "excelFile", excelFile));
        return new Prompt(promptTemplate.createMessage(), chatOptions);
    }

    public Prompt createAiAdviceNarrationPrompt(String adviceListItems, String attributeLevelTargets) {
        var promptTemplate = new PromptTemplate(aiAdviceNarrationPrompt, Map.of("adviceListItems", adviceListItems, "attributeLevelTargets", attributeLevelTargets));
        return new Prompt(promptTemplate.createMessage(), chatOptions);
    }
}
