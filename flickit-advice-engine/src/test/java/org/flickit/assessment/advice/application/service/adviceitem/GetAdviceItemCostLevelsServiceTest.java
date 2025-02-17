package org.flickit.assessment.advice.application.service.adviceitem;

import org.flickit.assessment.advice.application.port.in.adviceitem.GetAdviceItemCostLevelsUseCase;
import org.flickit.assessment.common.application.domain.adviceitem.CostLevel;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GetAdviceItemCostLevelsServiceTest {

    private final GetAdviceItemCostLevelsService service = new GetAdviceItemCostLevelsService();

    @Test
    void testGetAdviceCostLevel() {
        List<GetAdviceItemCostLevelsUseCase.AdviceItemCostLevel> items = Arrays.stream(CostLevel.values())
            .map(e -> new GetAdviceItemCostLevelsUseCase.AdviceItemCostLevel(e.getCode(), e.getTitle()))
            .toList();

        assertEquals(new GetAdviceItemCostLevelsUseCase.Result(items), service.getCostLevels());
    }
}
