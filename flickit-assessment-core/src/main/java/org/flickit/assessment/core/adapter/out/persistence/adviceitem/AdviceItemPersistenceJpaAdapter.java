package org.flickit.assessment.core.adapter.out.persistence.adviceitem;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.port.out.adviceitem.CountAdviceItemsPort;
import org.flickit.assessment.data.jpa.advice.adviceitem.AdviceItemJpaRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("coreAdviceItemPersistenceJpaAdapter")
@RequiredArgsConstructor
public class AdviceItemPersistenceJpaAdapter implements CountAdviceItemsPort {

    private final AdviceItemJpaRepository adviceItemRepository;

    @Override
    public int countAdviceItems(UUID assessmentResultId) {
        return adviceItemRepository.countByAssessmentResultId(assessmentResultId);
    }
}
