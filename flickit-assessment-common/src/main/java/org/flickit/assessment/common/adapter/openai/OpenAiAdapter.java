package org.flickit.assessment.common.adapter.openai;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.port.out.CallAiPromptPort;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OpenAiAdapter implements CallAiPromptPort {

    private final ChatModel chatModel;

    @Override
    public String call(Prompt prompt) {
        return chatModel
            .call(prompt)
            .getResult()
            .getOutput()
            .getContent();
    }
}
