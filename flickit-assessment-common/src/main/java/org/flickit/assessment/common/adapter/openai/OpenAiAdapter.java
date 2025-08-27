package org.flickit.assessment.common.adapter.openai;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.common.application.port.out.CallAiPromptPort;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpenAiAdapter implements CallAiPromptPort {

    private final ChatModel chatModel;

    @Override
    public <T> T call(Prompt prompt, Class<T> responseType) {
        long start = System.currentTimeMillis();
        T result = ChatClient.create(chatModel)
            .prompt()
            .system(prompt.getContents())
            .call()
            .entity(responseType);

        long end = System.currentTimeMillis();
        log.debug("OpenAiAdapter.call() - responseTime={}ms", end - start);
        return result;
    }
}
