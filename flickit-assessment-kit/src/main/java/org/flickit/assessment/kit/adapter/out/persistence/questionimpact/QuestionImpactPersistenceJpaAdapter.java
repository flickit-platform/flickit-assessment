package org.flickit.assessment.kit.adapter.out.persistence.questionimpact;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.kit.questionimpact.QuestionImpactJpaRepository;
import org.flickit.assessment.kit.application.domain.QuestionImpact;
import org.flickit.assessment.kit.application.port.out.questionimpact.LoadQuestionImpactsByQuestionPort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class QuestionImpactPersistenceJpaAdapter implements
    LoadQuestionImpactsByQuestionPort {

    private final QuestionImpactJpaRepository repository;

    @Override
    public List<QuestionImpact> loadByQuestionId(Long questionId) {
        return repository.findAllByQuestionId(questionId).stream()
            .map(QuestionImpactMapper::mapToDomainModel)
            .toList();
    }
}
