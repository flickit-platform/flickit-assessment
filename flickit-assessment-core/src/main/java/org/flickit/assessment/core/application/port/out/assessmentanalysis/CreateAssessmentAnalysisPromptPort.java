package org.flickit.assessment.core.application.port.out.assessmentanalysis;

import org.springframework.ai.chat.prompt.Prompt;

public interface CreateAssessmentAnalysisPromptPort {

    Prompt createAssessmentAnalysisPrompt(String assessmentTitle, String factSheet, String analysisType);
}
