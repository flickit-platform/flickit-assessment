package org.flickit.assessment.advice.application.port.out.advicenarration;

import org.springframework.ai.chat.prompt.Prompt;

public interface CreateAdviceAiNarrationPromptPort {

    Prompt createAdviceAiNarrationPrompt(String adviceListItems, String attributeLevelTargets);
}
