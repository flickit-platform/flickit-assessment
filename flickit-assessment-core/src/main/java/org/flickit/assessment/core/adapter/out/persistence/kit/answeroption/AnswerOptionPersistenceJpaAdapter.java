package org.flickit.assessment.core.adapter.out.persistence.kit.answeroption;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.domain.AnswerOption;
import org.flickit.assessment.core.application.port.out.answeroption.LoadAnswerOptionPort;
import org.flickit.assessment.data.jpa.kit.answeroption.AnswerOptionJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("coreAnswerOptionPersistenceJpaAdapter")
@RequiredArgsConstructor
public class AnswerOptionPersistenceJpaAdapter implements
    LoadAnswerOptionPort {

    private final AnswerOptionJpaRepository repository;

    @Override
    public Optional<AnswerOption> load(long answerOptionId, long kitVersionId) {
        return repository.findByIdAndKitVersionId(answerOptionId, kitVersionId)
            .map(AnswerOptionMapper::mapToDomainModel);
    }
}
