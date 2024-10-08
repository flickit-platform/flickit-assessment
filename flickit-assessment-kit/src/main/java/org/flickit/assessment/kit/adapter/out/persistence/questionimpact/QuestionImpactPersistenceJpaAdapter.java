package org.flickit.assessment.kit.adapter.out.persistence.questionimpact;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.data.jpa.kit.questionimpact.QuestionImpactJpaRepository;
import org.flickit.assessment.kit.application.domain.QuestionImpact;
import org.flickit.assessment.kit.application.port.out.questionimpact.CreateQuestionImpactPort;
import org.flickit.assessment.kit.application.port.out.questionimpact.DeleteQuestionImpactPort;
import org.flickit.assessment.kit.application.port.out.questionimpact.UpdateQuestionImpactByDslPort;
import org.flickit.assessment.kit.application.port.out.questionimpact.UpdateQuestionImpactPort;
import org.springframework.stereotype.Component;

import static org.flickit.assessment.kit.adapter.out.persistence.questionimpact.QuestionImpactMapper.mapToJpaEntityToPersist;
import static org.flickit.assessment.kit.common.ErrorMessageKey.QUESTION_IMPACT_ID_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class QuestionImpactPersistenceJpaAdapter implements
    CreateQuestionImpactPort,
    DeleteQuestionImpactPort,
    UpdateQuestionImpactByDslPort,
    UpdateQuestionImpactPort {

    private final QuestionImpactJpaRepository repository;

    @Override
    public Long persist(QuestionImpact impact) {
        return repository.save(mapToJpaEntityToPersist(impact)).getId();
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public void updateByDsl(UpdateQuestionImpactByDslPort.Param param) {
        repository.update(param.id(),
            param.weight(),
            param.questionId(),
            param.lastModificationTime(),
            param.lastModifiedBy());
    }

    @Override
    public void update(UpdateQuestionImpactPort.Param param) {
        if (!repository.existsByIdAndKitVersionId(param.questionImpactId(), param.kitVersionId()))
            throw new ResourceNotFoundException(QUESTION_IMPACT_ID_NOT_FOUND);

        repository.update(param.questionImpactId(),
            param.kitVersionId(),
            param.weight(),
            param.attributeId(),
            param.maturityLevelId(),
            param.lastModificationTime(),
            param.lastModifiedBy());
    }
}
