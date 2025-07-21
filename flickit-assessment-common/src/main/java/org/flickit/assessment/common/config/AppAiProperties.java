package org.flickit.assessment.common.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties("app.ai")
@RequiredArgsConstructor
public class AppAiProperties {

    private boolean enabled = false;

    private boolean saveAiInputFileEnabled = false;

    @Valid
    private Prompt prompt = new Prompt();

    @Setter
    @Getter
    @ToString
    public static class Prompt {

        @NotBlank
        private String attributeInsight = """
            As a software quality assessor, I have evaluated the {attributeTitle} maturity of a system for an assessment.
            We define {attributeTitle} as {attributeDescription}. The file content contains multiple-choice questions used to assess {attributeTitle}.
            The columns include the question, a hint, the weight of the question in calculating the overall score, and the actual score achieved by the software.
            Each question's weight reflects its importance and effectiveness, while the score—ranging between 0 and 1—indicates the strength of that question on the {attributeTitle} attribute.
            Both the weight and score contribute to reflecting the significance of each question within the assessment, indicating its impact on the overall maturity evaluation.
            Please generate an executive summary highlighting the main strengths and weaknesses in less than 100 words, presented as a single paragraph without extra line breaks.
            Start directly with specific strengths and weaknesses, avoiding introductory sentences. Consider discussing strengths and weaknesses.
            Use polite and considerate language, avoiding any derogatory terms, and do not mention the scores of individual questions.
            Please keep your summary descriptive and avoid prescribing actions or solutions. Do not include generic conclusions such as "Overall, the {attributeTitle} maturity level is deemed acceptable."
            Please provide the result in the {language} language. It is necessary that the result be an exact translation of the summary;
            Also, always use the name of the assessment and put the name of assessment in double quotations.
            Keep specialized computer science words in English, or if you are sure about their translation, include them with the English term in parentheses. Be aware of the word count limit.
            Here is the file content: {fileContent}.
            """;

        @NotBlank
        private String adviceNarrationAndAdviceItems = """
            Provide ALL output strictly in {language} language.
            For an assessment, an assessment platform has evaluated a software product by analyzing responses to various questions, each influencing specific quality attributes.
            The user has set maturity level targets for each attribute, and the platform has provided actionable advice items, highlighting which questions should be improved to achieve these targets.
            The advice includes the current status (selected option) and the goal status for each relevant question.
            Task: Based on the provided Advice Recommendations, generate up to 10 Advice Items including only as many points as there are distinct pieces of actionable advice. Each Advice Recommendation includes the following details:
                title: Generate a concise, action-driven title (max 100 characters) that starts with a strong verb and clearly conveys the intended action.
                description: Provide a detailed recommendations paragraph (max 1000 characters) explaining relevant technologies, methods, and tools. Discuss the best approach and viable alternatives, carefully analyzing their advantages, trade-offs, and potential challenges.
                    Justify why a particular option is recommended over others, considering factors such as scalability, maintainability, performance, security, and industry best practices.
                    Additionally, outline the expected benefits and risks of implementation, including potential impacts on development time, resource allocation, and long-term sustainability.
                    Where applicable, provide real-world examples or case studies to strengthen the recommendation.
                cost: between 0 to 2 where 0 LOW, 1 MEDIUM, 2 HIGH; This represents the estimated effort, time, and resources required to transition from the current state to the target goal, considering factors such as implementation complexity, required skill sets, tooling, and potential disruptions.
                    The larger the gap between the current status and the goal, the higher the cost.
                priority: between 0 to 2 where 0 LOW, 1 MEDIUM, 2 HIGH; This represents the urgency and significance of implementing this improvement in the context of software engineering best practices.
                    Factors influencing priority include the impact on software quality (e.g., security, performance, scalability, maintainability), business goals, regulatory compliance, and technical debt.
                    Higher priority items are those that, if left unaddressed, could lead to significant risks, inefficiencies, or long-term challenges.
                impact: between 0 to 2 where 0 LOW, 1 MEDIUM, 2 HIGH; This reflects the potential effect of implementing the change on the overall system. Impact takes into account how the change will influence key system attributes such as performance, scalability, security, maintainability, user experience, and business outcomes.
                    A higher impact indicates a more significant and transformative effect, such as major improvements in system efficiency or the resolution of critical issues, while a lower impact suggests incremental or localized changes.
            Additionally, provide a comprehensive paragraph discussing the significance of the suggested improvements and their expected outcomes. Explain how addressing these recommendations will enhance the targeted attributes, contributing to overall software quality.
            Emphasize the benefits of implementing these changes, such as improved scalability, security, maintainability, or performance. Also, highlight the potential consequences of neglecting these improvements. Ensure the paragraph clearly connects the recommended actions to their intended impact on the assessment goals.
            Ensure that the paragraph is fully connected to the advice items but remains clear and meaningful even when read independently, without direct reference to the individual items. It should provide a complete overview of why these improvements matter and how they align with the assessment’s goals.
            Also, always use the name of the assessment as it is, without translating it, and enclose it in double quotations.
            Wrap this paragraph in an HTML <p> tag without any class attributes. Ensure the response is concise but still fully translated into {language} language.
            Ensure that the advice is polite, constructive, and focused on actionable improvements while being tailored for an expert software assessor.
            Avoid referring to individual scores or negative phrasing. Keep the tone professional and supportive;
            Make sure the overall response size, including HTML tags, remains under 1000 characters and excludes any markdown.
            Attribute Targets: {attributeTargets}
            Advice Recommendations: {adviceRecommendations}
            """;
    }
}
