package org.flickit.assessment.advice.application.port.in.adviceitem;

import java.util.List;

public interface LoadAdviceItemCostLevelUseCase {

    Result getCostLevels();

    record Result(List<AdviceItemCostLevel> levels) { }

    record AdviceItemCostLevel(String code, String title) { }
}