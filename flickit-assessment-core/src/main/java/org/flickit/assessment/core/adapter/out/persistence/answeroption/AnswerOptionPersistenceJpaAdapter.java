package org.flickit.assessment.core.adapter.out.persistence.answeroption;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.port.out.answeroption.LoadAnswerOptionsByQuestionPort;
import org.flickit.assessment.core.application.domain.AnswerOption;
import org.flickit.assessment.data.jpa.kit.answeroption.AnswerOptionJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("coreAnswerOptionPersistenceJpaAdapter")
@RequiredArgsConstructor
public class AnswerOptionPersistenceJpaAdapter implements LoadAnswerOptionsByQuestionPort {

    private final AnswerOptionJpaRepository repository;

    @Override
    public List<AnswerOption> loadByQuestionId(Long questionId, Long kitVersionId) {
        return repository.findByQuestionIdAndKitVersionId(questionId, kitVersionId).stream()
            .map(AnswerOptionMapper::mapToDomainModel)
            .toList();
    }
}
