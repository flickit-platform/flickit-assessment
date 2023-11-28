package org.flickit.assessment.kit.adapter.out.persistence.answeroption;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.kit.answeroption.AnswerOptionJpaRepository;
import org.flickit.assessment.kit.application.domain.AnswerOption;
import org.flickit.assessment.kit.application.port.out.answeroption.LoadAnswerOptionByIndexPort;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AnswerOptionPersistenceJpaAdapter implements LoadAnswerOptionByIndexPort {

    private final AnswerOptionJpaRepository repository;

    @Override
    public AnswerOption loadByIndex(int index, Long questionId) {
        return AnswerOptionMapper.mapToDomainModel(repository.findByIndexAndQuestionId(index, questionId));
    }
}
