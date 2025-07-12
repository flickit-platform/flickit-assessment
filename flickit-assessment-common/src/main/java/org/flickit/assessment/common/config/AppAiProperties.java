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
            As a software quality assessor, I have evaluated the {attributeTitle} maturity of a system for an assessment titled {assessmentTitle}.
            We define {attributeTitle} as {attributeDescription}. The file content contains multiple-choice questions used to assess {attributeTitle}.
            The columns include the question, a hint, the weight of the question in calculating the overall score, and the actual score achieved by the software.
            Each question's weight reflects its importance and effectiveness, while the score—ranging between 0 and 1—indicates the strength of that question on the {attributeTitle} attribute.
            Both the weight and score contribute to reflecting the significance of each question within the assessment, indicating its impact on the overall maturity evaluation.
            Please generate an executive summary highlighting the main strengths and weaknesses in less than 100 words, presented as a single paragraph without extra line breaks.
            Start directly with specific strengths and weaknesses, avoiding introductory sentences. Consider the use of the assessment title ("{assessmentTitle}") when discussing strengths and weaknesses.
            Use polite and considerate language, avoiding any derogatory terms, and do not mention the scores of individual questions.
            Please keep your summary descriptive and avoid prescribing actions or solutions. Do not include generic conclusions such as "Overall, the {attributeTitle} maturity level is deemed acceptable."
            Please provide the result in the {language} language. It is necessary that the result be an exact translation of the summary, except for the assessment name ("{assessmentTitle}"), which must remain untranslated;
            Also, always use the name of the assessment and put the name of assessment in double quotations.
            Keep specialized computer science words in English, or if you are sure about their translation, include them with the English term in parentheses. Be aware of the word count limit.
            Here is the file content: {fileContent}.
            """;

        @NotBlank
        private String adviceNarrationAndAdviceItems = """
            Provide all output strictly in {language}.
            For an assessment titled "{assessmentTitle}", a software product has been evaluated by analyzing responses to various questions, each influencing specific quality attributes.
            The user has set maturity level targets for each attribute, and the platform has generated actionable advice items indicating which questions need improvement to meet these targets. Each advice item includes the current status (selected option) and the goal status.
            Task: Based on the provided Advice Recommendations, generate up to 10 concise Advice Items. Include only as many points as there are distinct actionable advices. For each Advice Item, provide the following:
            - title: A concise, action-driven title (max 100 characters) starting with a strong verb clearly conveying the intended action.
            - description: A detailed recommendation paragraph (max 1000 characters) covering relevant technologies, methods, and tools. Discuss the best approach and viable alternatives, analyzing advantages, trade-offs, and challenges. Justify why the recommended option is preferred, considering scalability, maintainability, performance, security, and industry best practices. Outline expected benefits and risks, including impacts on development time, resources, and long-term sustainability. Include real-world examples or case studies where appropriate.
            - cost: Integer 0 (LOW), 1 (MEDIUM), or 2 (HIGH), estimating effort, time, and resources needed to move from current to target state. Larger gaps imply higher cost.
            - priority: Integer 0 (LOW), 1 (MEDIUM), or 2 (HIGH), indicating urgency and significance based on software quality, business goals, compliance, and technical debt. Higher priority means unaddressed issues could cause major risks or inefficiencies.
            - impact: Integer 0 (LOW), 1 (MEDIUM), or 2 (HIGH), reflecting the potential positive effect on system attributes like performance, scalability, security, maintainability, user experience, and business outcomes. Higher impact means transformative improvements; lower impact means incremental changes.
            Please normalize the priority, impact, and cost values across the advice items so they are distributed proportionally rather than mostly high values.
            Additionally, provide a comprehensive, standalone paragraph wrapped in an HTML `<p>` tag (no class attributes), clearly referencing the assessment titled "{assessmentTitle}" (keep the name in English and in double quotes). This paragraph should:
            - Explain the overall significance of the suggested improvements and their expected outcomes.
            - Highlight how addressing these recommendations will enhance the targeted attributes and overall software quality.
            - Emphasize benefits like improved scalability, security, maintainability, or performance.
            - Warn about potential consequences of neglecting these improvements.
            - Connect clearly to the advice items but remain meaningful if read independently.
            - Be concise and fully translated into {language}.

            Ensure the advice is polite, constructive, actionable, and tailored for an expert software assessor.
            Avoid referring to individual scores or negative phrasing. Maintain a professional and supportive tone.
            Keep the total response length under 1000 characters, including the HTML paragraph tags, and exclude any markdown formatting.
            Attribute Targets: {attributeTargets}
            Advice Recommendations: {adviceRecommendations}
            """;
    }
}
