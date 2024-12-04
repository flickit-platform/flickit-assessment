package org.flickit.assessment.advice.application.port.in.adviceitem;

import java.util.List;

public interface GetAdviceItemPriorityLevelsUseCase {

    Result getPriorityLevels();

    record Result(List<AdviceItemPriorityLevel> levels) { }

    record AdviceItemPriorityLevel(String code, String title) { }
}
