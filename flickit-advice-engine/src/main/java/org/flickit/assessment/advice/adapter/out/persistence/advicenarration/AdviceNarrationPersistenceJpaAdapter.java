package org.flickit.assessment.advice.adapter.out.persistence.advicenarration;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.advice.application.domain.AdviceNarration;
import org.flickit.assessment.advice.application.port.out.advicenarration.LoadAdviceNarrationPort;
import org.flickit.assessment.data.jpa.advice.advicenarration.AdviceNarrationJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AdviceNarrationPersistenceJpaAdapter implements LoadAdviceNarrationPort {

    private final AdviceNarrationJpaRepository adviceNarrationRepository;

    @Override
    public Optional<AdviceNarration> loadAdviceNarration(UUID assessmentResultId) {
        return adviceNarrationRepository.findByAssessmentResultId(assessmentResultId)
            .map(AdviceNarrationMapper::mapToDomainModel);
    }
}
