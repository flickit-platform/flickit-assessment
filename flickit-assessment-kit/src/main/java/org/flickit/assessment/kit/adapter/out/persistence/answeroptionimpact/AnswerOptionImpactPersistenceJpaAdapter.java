package org.flickit.assessment.kit.adapter.out.persistence.answeroptionimpact;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.kit.asnweroptionimpact.AnswerOptionImpactJpaRepository;
import org.flickit.assessment.data.jpa.kit.questionimpact.QuestionImpactJpaEntity;
import org.flickit.assessment.data.jpa.kit.questionimpact.QuestionImpactJpaRepository;
import org.flickit.assessment.kit.application.port.out.answeroptionimpact.CreateAnswerOptionImpactPort;
import org.flickit.assessment.kit.application.port.out.answeroptionimpact.UpdateAnswerOptionImpactPort;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AnswerOptionImpactPersistenceJpaAdapter implements
    CreateAnswerOptionImpactPort,
    UpdateAnswerOptionImpactPort {

    private final AnswerOptionImpactJpaRepository repository;
    private final QuestionImpactJpaRepository questionImpactRepository;

    @Override
    public Long persist(CreateAnswerOptionImpactPort.Param param) {
        QuestionImpactJpaEntity questionImpactJpaEntity = questionImpactRepository.getReferenceById(param.questionImpactId());
        return repository.save(AnswerOptionImpactMapper.mapToJpaEntity(param, questionImpactJpaEntity)).getId();
    }

    @Override
    public void update(UpdateAnswerOptionImpactPort.Param param) {
        repository.update(param.id(),
            param.value(),
            param.lastModificationTime(),
            param.lastModifiedBy());
    }
}
