package org.flickit.assessment.core.adapter.out.persistence.advicenarration;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.port.out.advicenarration.LoadAdviceNarrationPort;
import org.flickit.assessment.data.jpa.advice.advicenarration.AdviceNarrationJpaRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("coreAdviceNarrationPersistenceJpaAdapter")
@RequiredArgsConstructor
public class AdviceNarrationPersistenceJpaAdapter implements LoadAdviceNarrationPort {

    private final AdviceNarrationJpaRepository repository;

    @Override
    public String load(UUID assessmentResultId) {
        var narration = repository.findByAssessmentResultId(assessmentResultId).orElse(null);

        if (narration == null || (narration.getAiNarration() == null && narration.getAssessorNarration() == null))
            return null;
        if (narration.getAssessorNarration() == null ||
            narration.getAiNarrationTime() != null && narration.getAiNarrationTime().isAfter(narration.getAssessorNarrationTime())) {
            return narration.getAiNarration();
        }
        return narration.getAssessorNarration();
    }
}
