package org.flickit.assessment.advice.application.service.adviceitem;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.advice.application.domain.adviceitem.ImpactLevel;
import org.flickit.assessment.advice.application.port.in.adviceitem.GetAdviceItemImpactLevelsUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetAdviceItemImpactLevelsService implements GetAdviceItemImpactLevelsUseCase {

    @Override
    public Result getImpactLevels() {
        List<AdviceItemImpactLevel> items = Arrays.stream(ImpactLevel.values())
            .map(e -> new AdviceItemImpactLevel(e.getCode(), e.getTitle()))
            .toList();
        return new Result(items);
    }
}
