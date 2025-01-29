package org.flickit.assessment.advice.adapter.out.openai;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.advice.application.port.out.advicenarration.GenerateAiAdvicePort;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

@Component("adviceOpenAiAdapter")
@RequiredArgsConstructor
public class OpenAiAdapter implements GenerateAiAdvicePort {

    private final ChatModel chatModel;

    @Override
    public GenerateAiAdvicePort.Result generateAiAdviceNarrationAndItems(String prompt) {
        return ChatClient.create(chatModel)
            .prompt()
            .system(prompt)
            .call()
            .entity(new ParameterizedTypeReference<>() {
            });
    }
}
