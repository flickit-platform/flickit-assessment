package org.flickit.assessment.core.adapter.out.openai;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.AppAiProperties;
import org.flickit.assessment.common.config.OpenAiProperties;
import org.flickit.assessment.core.application.domain.AssessmentAnalysisInsight;
import org.flickit.assessment.core.application.port.out.assessmentanalysis.CreateAssessmentAnalysisPromptPort;
import org.flickit.assessment.core.application.port.out.attributeinsight.CreateAttributeAiInsightPromptPort;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("coreOpenAiAdapter")
@RequiredArgsConstructor
public class OpenAiAdapter implements
    CreateAttributeAiInsightPromptPort,
    CreateAssessmentAnalysisPromptPort {

    private final OpenAiProperties openAiProperties;
    private final AppAiProperties appAiProperties;

    @Override
    public Prompt createAssessmentAnalysisPrompt(String assessmentTitle, String factSheet, String analysisType) {
        var converter = new BeanOutputConverter<>(AssessmentAnalysisInsight.class);
        return new Prompt(new PromptTemplate(appAiProperties.getTemplate(), Map.of("title", assessmentTitle, "factSheet", factSheet,
            "analysisType", analysisType, "format", converter.getFormat())).createMessage());
    }

    @Override
    public Prompt createAttributeAiInsightPrompt(String title, String description, String excelFile) {
        var promptTemplate = new PromptTemplate(appAiProperties.getAttributeAiInsightPrompt(), Map.of("title", title, "description", description, "excelFile", excelFile));
        return new Prompt(promptTemplate.createMessage(), openAiProperties.getChatOptions());
    }
}
