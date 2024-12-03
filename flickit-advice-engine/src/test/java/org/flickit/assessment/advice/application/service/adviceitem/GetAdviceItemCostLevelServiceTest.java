package org.flickit.assessment.advice.application.service.adviceitem;

import org.flickit.assessment.advice.application.domain.adviceitem.CostLevel;
import org.flickit.assessment.advice.application.port.in.adviceitem.LoadAdviceItemCostLevelUseCase;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GetAdviceItemCostLevelServiceTest {

    private final GetAdviceItemCostLevelService service = new GetAdviceItemCostLevelService();

    @Test
    void testGetAdviceCostLevel() {
        List<LoadAdviceItemCostLevelUseCase.AdviceItemCostLevel> items = Arrays.stream(CostLevel.values())
            .map(e -> new LoadAdviceItemCostLevelUseCase.AdviceItemCostLevel(e.getCode(), e.getTitle()))
            .toList();

        assertEquals(new LoadAdviceItemCostLevelUseCase.Result(items), service.getCostLevels());
    }
}
