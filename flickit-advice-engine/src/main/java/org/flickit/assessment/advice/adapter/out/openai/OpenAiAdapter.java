package org.flickit.assessment.advice.adapter.out.openai;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.advice.application.port.out.adviceitem.CreateAiAdviceItemsPort;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

@Component("adviceOpenAiAdapter")
@RequiredArgsConstructor
public class OpenAiAdapter implements CreateAiAdviceItemsPort {

    private final ChatModel chatModel;

    @Override
    public CreateAiAdviceItemsPort.Result generateAiAdviceItems(String prompt) {
        return ChatClient.create(chatModel)
            .prompt()
            .system(prompt)
            .call()
            .entity(new ParameterizedTypeReference<>() {
            });
    }
}
