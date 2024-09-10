package org.flickit.assessment.common.config;

import lombok.Builder;
import lombok.Getter;
import org.springframework.ai.chat.prompt.ChatOptions;

import java.util.List;

@Getter
@Builder(toBuilder = true)
public class DefaultChatOptions implements ChatOptions {

    @Builder.Default
    private String model = "system";
    @Builder.Default
    private Float temperature= 0.7f;
    private Float frequencyPenalty;
    private Integer maxTokens;
    private Float presencePenalty;
    private List<String> stopSequences;
    private Integer topK;
    private Float topP;

    @Override
    public ChatOptions copy() {
        return this.toBuilder().build();
    }
}
