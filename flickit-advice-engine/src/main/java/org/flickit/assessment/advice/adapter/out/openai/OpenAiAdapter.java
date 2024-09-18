package org.flickit.assessment.advice.adapter.out.openai;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.advice.application.port.out.advicenarration.CreateAdviceAiNarrationPromptPort;
import org.flickit.assessment.common.config.AppAiProperties;
import org.flickit.assessment.common.config.OpenAiProperties;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("adviceOpenAiAdapter")
@RequiredArgsConstructor
public class OpenAiAdapter implements CreateAdviceAiNarrationPromptPort {

    private final AppAiProperties appAiProperties;
    private final OpenAiProperties openAiProperties;

    @Override
    public Prompt createAdviceAiNarrationPrompt(String adviceListItems, String attributeLevelTargets) {
        var promptTemplate = new PromptTemplate(appAiProperties.getAdviceAiNarrationPromptTemplate(), Map.of("adviceListItems", adviceListItems, "attributeLevelTargets", attributeLevelTargets));
        return new Prompt(promptTemplate.createMessage(), openAiProperties.getChatOptions());
    }
}
