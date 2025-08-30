package org.flickit.assessment.core.application.port.in.adviceitem;

import java.util.List;

public interface GetAdviceItemCostLevelsUseCase {

    Result getCostLevels();

    record Result(List<AdviceItemCostLevel> levels) { }

    record AdviceItemCostLevel(String code, String title) { }
}
