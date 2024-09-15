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
        We define {title} as {description} The uploaded Excel file contains multiple-choice questions used to assess {title}.
        The Excel columns include the question, a hint, the weight of the question in calculating the overall score,
         and the actual score achieved by the software. Please generate an executive summary highlighting the main strengths and weaknesses in less than 100 words.
        Use polite and considerate language, avoiding any derogatory terms, and do not mention the scores of individual questions.
        Here is the uploaded Excel file: {excelFile}.
        """;

    public Prompt createAttributeAiInsightPrompt(String title, String description, String excelFile) {
        var promptTemplate = new PromptTemplate(attributeAiInsightPrompt, Map.of("title", title, "description", description, "excelFile", excelFile));
        return new Prompt(promptTemplate.createMessage(), chatOptions);
    }

    public Prompt createAssessmentAnalysisPrompt(String assessmentTitle, String factSheet, String analysisType, String format) {
        String template = """
            Generate detailed information for the assessment: {title}.
            This the FactSheet about the result: {factSheet}.
            About FactSheet: The table provides an overview of the software quality aspects for different repositories in an organization. Each repository's status is evaluated based on technical debt, maintainability grade and issue count, reliability grade and issue count, security grade and issue count, and test coverage. Note that the above table is just an example, and values may vary.
            This information should be about this analysis type: {analysisType}.
            1. Provide an analysis about ProgrammingLanguages according to the Provided factSheet in one paragraph.
            2. Provide an analysis about OverallCodeQuality according to the Provided factSheet in one paragraph.
            3. Provide an analysis about CodeComplexity according to the Provided factSheet in one paragraph.
            4. Provide an analysis text about CodeSmell according to the Provided factSheet in one paragraph.
            5. Provide an analysis text about CodeDuplication according to the Provided factSheet in one paragraph.
            6. Provide an analysis text about CodeOrganization according to the Provided factSheet in one paragraph.
            7. Provide an analysis text about CodeReliability according to the Provided factSheet in one paragraph.
            8. Provide an analysis text about CodeSecurity according to the Provided factSheet in one paragraph.
            9. Provide an analysis text about CodeReliability according to the Provided factSheet in one paragraph.
            10. Provide a random text about CodeSecurity according to the Provided factSheet in one paragraph.
            11. Provide a random text about AutomatedTestCoverage  according to the Provided factSheet in one paragraph.
            12. Provide a random text about CodeSecurity according to the Provided factSheet in one sentence.
            Only use english chars.
            Please provide the data in the following JSON format: {format}
            """;

        return new Prompt(new PromptTemplate(
            template, Map.of("title", assessmentTitle, "factSheet", factSheet,
            "analysisType", analysisType, "format", format)).createMessage());
    }
}
