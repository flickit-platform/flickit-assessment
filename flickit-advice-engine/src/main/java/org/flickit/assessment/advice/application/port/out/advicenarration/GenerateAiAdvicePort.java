package org.flickit.assessment.advice.application.port.out.advicenarration;

import java.util.List;

public interface GenerateAiAdvicePort {

    Result generateAiAdviceNarrationAndItems(String prompt);

    record Result(String aiNarration, List<AdviceItem> adviceItems) {

        public record AdviceItem(String title,
                                 String description,
                                 int cost,
                                 int priority,
                                 int impact) {
        }
    }
}
