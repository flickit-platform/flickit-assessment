package org.flickit.assessment.kit.adapter.out.persistence.answeroptionimpact;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.kit.asnweroptionimpact.AnswerOptionImpactJpaRepository;
import org.flickit.assessment.kit.application.port.out.answeroptionimpact.UpdateAnswerOptionImpactPort;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AnswerOptionImpactPersistenceJpaAdapter implements
    UpdateAnswerOptionImpactPort {

    private final AnswerOptionImpactJpaRepository repository;

    @Override
    public void update(UpdateAnswerOptionImpactPort.Param param) {
        repository.update(param.id(),
            param.kitVersionId(),
            param.value(),
            param.lastModificationTime(),
            param.lastModifiedBy());
    }
}
