package org.flickit.assessment.advice.adapter.out.openai;

import lombok.RequiredArgsConstructor;

import org.flickit.assessment.advice.application.port.out.advicenarration.CreateAdviceAiNarrationPort;
import org.flickit.assessment.common.config.OpenAiProperties;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Component;

@Component("coreOpenAiAdapter")
@RequiredArgsConstructor
public class OpenAiAdapter implements CreateAdviceAiNarrationPort {

    private final OpenAiProperties openAiProperties;
    private final ChatModel chatModel;

    @Override
    public String createAdviceAiNarration(String adviceListItems, String attributeLevelTargets) {
        var prompt = openAiProperties.createAdviceAiNarration(adviceListItems, attributeLevelTargets);
        String result = chatModel
            .call(prompt)
            .getResult()
            .getOutput()
            .getContent();
        return "<p>" + result + "</p>";
    }
}

