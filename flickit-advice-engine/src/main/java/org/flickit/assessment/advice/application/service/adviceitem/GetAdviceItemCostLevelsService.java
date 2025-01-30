package org.flickit.assessment.advice.application.service.adviceitem;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.advice.application.port.in.adviceitem.GetAdviceItemCostLevelsUseCase;
import org.flickit.assessment.common.application.domain.adviceitem.CostLevel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetAdviceItemCostLevelsService implements GetAdviceItemCostLevelsUseCase {

    @Override
    public Result getCostLevels() {
        List<AdviceItemCostLevel> items = Arrays.stream(CostLevel.values())
            .map(e -> new AdviceItemCostLevel(e.getCode(), e.getTitle()))
            .toList();
        return new Result(items);
    }
}
