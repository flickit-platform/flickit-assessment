package org.flickit.assessment.advice.application.service.adviceitem;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.advice.application.domain.adviceitem.PriorityLevel;
import org.flickit.assessment.advice.application.port.in.adviceitem.GetAdviceItemPriorityLevelsUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetAdviceItemPriorityLevelsService implements GetAdviceItemPriorityLevelsUseCase {

    @Override
    public Result getPriorityLevels() {
        List<AdviceItemPriorityLevel> items = Arrays.stream(PriorityLevel.values())
            .map(e -> new AdviceItemPriorityLevel(e.getCode(), e.getTitle()))
            .toList();
        return new Result(items);
    }
}
