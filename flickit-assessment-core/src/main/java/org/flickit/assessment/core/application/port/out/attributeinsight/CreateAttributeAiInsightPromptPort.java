package org.flickit.assessment.core.application.port.out.attributeinsight;

import org.springframework.ai.chat.prompt.Prompt;

public interface CreateAttributeAiInsightPromptPort {

    Prompt createAttributeAiInsightPrompt(String title, String description, String excelFile);
}
