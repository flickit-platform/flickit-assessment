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
        As a software quality assessor, I have evaluated the {attributeTitle} maturity of a system for an assessment titled {assessmentTitle}.
        We define {attributeTitle} as {attributeDescription}. The file content contains multiple-choice questions used to assess {attributeTitle}.
        The columns include the question, a hint, the weight of the question in calculating the overall score, and the actual score achieved by the software.
        Each question's weight reflects its importance and effectiveness, while the score—ranging between 0 and 1—indicates the strength of that question on the {attributeTitle} attribute.
        Both the weight and score contribute to reflecting the significance of each question within the assessment, indicating its impact on the overall maturity evaluation.
        Please generate an executive summary highlighting the main strengths and weaknesses in less than 100 words, presented as a single paragraph without extra line breaks.
        Start directly with specific strengths and weaknesses, avoiding introductory sentences. Consider the use of the assessment title ("{assessmentTitle}") when discussing strengths and weaknesses.
        Use polite and considerate language, avoiding any derogatory terms, and do not mention the scores of individual questions.
        Please keep your summary descriptive and avoid prescribing actions or solutions. Do not include generic conclusions such as "Overall, the {attributeTitle} maturity level is deemed acceptable."
        Please recognize the language of the questions in the provided file content and provide the results in that language. It is necessary that the result be an exact translation of the summary, except for the assessment name ("{assessmentTitle}"), which must remain untranslated.
        Keep specialized computer science words in English, or if you are sure about their translation, include them with the English term in parentheses. Be aware of the word count limit.
        Here is the file content: {fileContent}.
        """;

    private String adviceNarrationAndItemsPrompt = """
        For an assessment, titled "%s", an assessment platform has evaluated a software product by analyzing responses to various questions, each influencing specific quality attributes.
        The user has set maturity level targets for each attribute, and the platform has provided actionable advice items, highlighting which questions should be improved to achieve these targets.
        The advice includes the current status (selected option) and the goal status for each relevant question.
        Task: Based on the provided Advice Recommendations, generate up to 10 Advice Items including only as many points as there are distinct pieces of actionable advice. Each Advice Recommendation includes the following details:
            title : Up to 100 characters
            description : Up to 3000 characters
            cost : between 0 to 2 where 0 LOW, 1 MEDIUM, 2 HIGH;
            priority : between 0 to 2 where 0 LOW, 1 MEDIUM, 2 HIGH;
            impact : between 0 to 2 where 0 LOW, 1 MEDIUM, 2 HIGH;
        Additionally, provide a complete paragraph mentioning the attributes, their target levels, and the related advice and suggestions.
        Wrap this paragraph in an HTML <p> tag without any class attributes. Also, include the title of the assessment in your response.
        Ensure that the advice is polite, constructive, and focused on actionable improvements while being tailored for an expert software assessor.
        Avoid referring to individual scores or negative phrasing. Keep the tone professional and supportive.
        Attribute Targets: %s
        Advice Recommendations: %s

        Make sure the overall response size, including HTML tags, remains under 1000 characters and excludes any markdown.
        """;

    public Prompt createAttributeAiInsightPrompt(String attributeTitle, String attributeDescription, String assessmentTitle, String fileContent) {
        var promptTemplate = new PromptTemplate(attributeAiInsightPrompt, Map.of("attributeTitle", attributeTitle, "attributeDescription", attributeDescription, "assessmentTitle", assessmentTitle, "fileContent", fileContent));
        return new Prompt(promptTemplate.createMessage(), chatOptions);
    }

    public String createAiAdviceNarrationAndItemsPrompt(String assessmentTitle, String attributeLevelTargets, String adviceRecommendations) {
        return String.format(adviceNarrationAndItemsPrompt, assessmentTitle, attributeLevelTargets, adviceRecommendations);
    }
}
