package org.flickit.assessment.kit.adapter.out.persistence.questionimpact;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.kit.questionimpact.QuestionImpactJpaRepository;
import org.flickit.assessment.kit.application.domain.QuestionImpact;
import org.flickit.assessment.kit.application.port.out.questionimpact.CreateQuestionImpactPort;
import org.flickit.assessment.kit.application.port.out.questionimpact.DeleteQuestionImpactPort;
import org.flickit.assessment.kit.application.port.out.questionimpact.UpdateQuestionImpactPort;
import org.springframework.stereotype.Component;

import static org.flickit.assessment.kit.adapter.out.persistence.questionimpact.QuestionImpactMapper.mapToJpaEntityToPersist;

@Component
@RequiredArgsConstructor
public class QuestionImpactPersistenceJpaAdapter implements
    CreateQuestionImpactPort,
    DeleteQuestionImpactPort,
    UpdateQuestionImpactPort {

    private final QuestionImpactJpaRepository repository;

    @Override
    public Long persist(QuestionImpact impact) {
        return repository.save(mapToJpaEntityToPersist(impact)).getId();
    }

    @Override
    public void delete(Long questionImpactId, Long kitVersionId) {
        repository.deleteByIdAndKitVersionId(questionImpactId, kitVersionId);
    }

    @Override
    public void update(UpdateQuestionImpactPort.Param param) {
        repository.update(param.id(),
            param.kitVersionId(),
            param.weight(),
            param.questionId(),
            param.lastModificationTime(),
            param.lastModifiedBy());
    }
}
