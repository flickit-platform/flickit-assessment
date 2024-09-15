package org.flickit.assessment.core.adapter.out.openai;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.flickit.assessment.common.config.OpenAiProperties;
import org.flickit.assessment.core.application.domain.AnalysisType;
import org.flickit.assessment.core.application.domain.AssessmentAnalysisInsight;
import org.flickit.assessment.core.application.domain.Attribute;
import org.flickit.assessment.core.application.port.out.assessment.CreateAssessmentAiAnalysisPort;
import org.flickit.assessment.core.application.port.out.attribute.CreateAttributeAiInsightPort;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OpenAiAdapter implements
    CreateAttributeAiInsightPort,
    CreateAssessmentAiAnalysisPort {

    private final OpenAiProperties openAiProperties;
    private final ChatModel chatModel;

    @SneakyThrows
    @Override
    public String generateInsight(String fileText, Attribute attribute) {
        var prompt = openAiProperties.createAttributeAiInsightPrompt(attribute.getTitle(), attribute.getDescription(), fileText);
        return chatModel
            .call(prompt)
            .getResult()
            .getOutput()
            .getContent();
    }

    @Override
    public AssessmentAnalysisInsight generateAssessmentAnalysis(String assessmentTitle, String factSheet, AnalysisType analysisType) {
        BeanOutputConverter<AssessmentAnalysisInsight> converter = new BeanOutputConverter<>(AssessmentAnalysisInsight.class);
        String format = converter.getFormat();
        Prompt prompt = openAiProperties.createAssessmentAnalysisPrompt(assessmentTitle, factSheet, analysisType.name(), format);

        var generation = chatModel.call(prompt).getResult();
        return converter.convert(generation.getOutput().getContent());
    }
}
