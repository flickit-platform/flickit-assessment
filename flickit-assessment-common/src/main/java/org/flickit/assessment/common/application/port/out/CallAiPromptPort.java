package org.flickit.assessment.common.application.port.out;

import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.core.ParameterizedTypeReference;

public interface CallAiPromptPort {

    String call(Prompt prompt);

    <T> T call(String prompt, ParameterizedTypeReference<T> responseType);
}
