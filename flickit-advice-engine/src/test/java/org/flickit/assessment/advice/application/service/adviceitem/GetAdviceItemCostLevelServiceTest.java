package org.flickit.assessment.advice.application.service.adviceitem;

import org.flickit.assessment.advice.application.domain.adviceitem.CostLevel;
import org.flickit.assessment.advice.application.port.in.adviceitem.GetAdviceItemCostLevelUseCase;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GetAdviceItemCostLevelServiceTest {

    private final GetAdviceItemCostLevelService service = new GetAdviceItemCostLevelService();

    @Test
    void testGetAdviceCostLevel() {
        List<GetAdviceItemCostLevelUseCase.AdviceItemCostLevel> items = Arrays.stream(CostLevel.values())
            .map(e -> new GetAdviceItemCostLevelUseCase.AdviceItemCostLevel(e.getCode(), e.getTitle()))
            .toList();

        assertEquals(new GetAdviceItemCostLevelUseCase.Result(items), service.getCostLevels());
    }
}
