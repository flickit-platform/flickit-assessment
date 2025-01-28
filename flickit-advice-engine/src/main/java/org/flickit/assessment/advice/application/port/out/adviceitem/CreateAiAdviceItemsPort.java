package org.flickit.assessment.advice.application.port.out.adviceitem;

import java.util.List;

public interface CreateAiAdviceItemsPort {

    Result generateAiAdviceItems(String prompt);

    record Result(String aiNarration, List<AdviceItem> adviceItems) {

        public record AdviceItem(String title,
                                 String description,
                                 int cost,
                                 int priority,
                                 int impact) {
        }
    }
}
