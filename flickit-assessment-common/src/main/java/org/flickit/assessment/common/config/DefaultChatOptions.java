package org.flickit.assessment.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.lang.Nullable;

import java.util.List;

@Getter
@Setter
public class DefaultChatOptions implements ChatOptions {

    @Nullable
    private String model = "gpt-4o-mini";

    @Nullable
    private Float frequencyPenalty;

    @Nullable
    private Integer maxTokens;

    @Nullable
    private Float presencePenalty;

    @Nullable
    private List<String> stopSequences;

    @Nullable
    private Float temperature = 0.7f;

    @Nullable
    private Integer topK;

    @Nullable
    private Float topP;

    @Override
    public ChatOptions copy() {
        DefaultChatOptions copy = new DefaultChatOptions();
        copy.setModel(this.model);
        copy.setFrequencyPenalty(this.frequencyPenalty);
        copy.setMaxTokens(this.maxTokens);
        copy.setPresencePenalty(this.presencePenalty);
        copy.setStopSequences(this.stopSequences);
        copy.setTemperature(this.temperature);
        copy.setTopK(this.topK);
        copy.setTopP(this.topP);
        return copy;
    }
}
