package org.flickit.assessment.advice.adapter.out.persistence.advicenarration;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.advice.application.domain.AdviceNarration;
import org.flickit.assessment.advice.application.port.out.advicenarration.CreateAdviceNarrationPort;
import org.flickit.assessment.advice.application.port.out.advicenarration.LoadAdviceNarrationPort;
import org.flickit.assessment.advice.application.port.out.advicenarration.UpdateAdviceNarrationPort;
import org.flickit.assessment.data.jpa.advice.advicenarration.AdviceNarrationJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AdviceNarrationPersistenceJpaAdapter implements
    CreateAdviceNarrationPort,
    UpdateAdviceNarrationPort,
    LoadAdviceNarrationPort {

    private final AdviceNarrationJpaRepository repository;

    @Override
    public void persist(AdviceNarration adviceNarration) {
        repository.save(AdviceNarrationMapper.toJpaEntity(adviceNarration));
    }

    @Override
    public void updateAssessorNarration(AssessorNarrationParam param) {
        repository.updateAssessorNarration(param.id(),
            param.narration(),
            param.narrationTime(),
            param.createdBy());
    }

    @Override
    public void updateAiNarration(AiNarrationParam param) {
        repository.updateAiNarration(param.id(),
            param.narration(),
            param.narrationTime());
    }

    @Override
    public Optional<AdviceNarration> loadByAssessmentResultId(UUID assessmentResultId) {
        return repository.findByAssessmentResultId(assessmentResultId)
            .map(AdviceNarrationMapper::toDomain);
    }
}
