package org.flickit.assessment.core.adapter.out.openai;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.flickit.assessment.common.config.OpenAiProperties;
import org.flickit.assessment.core.application.domain.Attribute;
import org.flickit.assessment.core.application.port.out.attribute.CreateAttributeAiInsightPort;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OpenAiAdapter implements CreateAttributeAiInsightPort {

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
}
