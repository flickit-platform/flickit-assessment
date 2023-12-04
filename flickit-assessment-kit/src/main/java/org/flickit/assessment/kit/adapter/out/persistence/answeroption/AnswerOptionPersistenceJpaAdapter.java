package org.flickit.assessment.kit.adapter.out.persistence.answeroption;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.kit.answeroption.AnswerOptionJpaRepository;
import org.flickit.assessment.kit.application.domain.AnswerOption;
import org.flickit.assessment.kit.application.port.out.answeroption.CreateAnswerOptionPort;
import org.flickit.assessment.kit.application.port.out.answeroption.LoadAnswerOptionByIndexPort;
import org.flickit.assessment.kit.application.port.out.answeroption.LoadAnswerOptionsByQuestionPort;
import org.flickit.assessment.kit.application.port.out.answeroption.UpdateAnswerOptionPort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AnswerOptionPersistenceJpaAdapter implements
    UpdateAnswerOptionPort,
    LoadAnswerOptionsByQuestionPort,
    LoadAnswerOptionByIndexPort,
    CreateAnswerOptionPort {

    private final AnswerOptionJpaRepository repository;

    @Override
    public void update(UpdateAnswerOptionPort.Param param) {
        repository.update(param.id(), param.title());
    }

    @Override
    public List<AnswerOption> loadByQuestionId(Long questionId) {
        return repository.findByQuestionId(questionId).stream()
            .map(AnswerOptionMapper::mapToDomainModel)
            .toList();
    }

    @Override
    public AnswerOption loadByIndex(Integer index, Long questionId) {
        return AnswerOptionMapper.mapToDomainModel(repository.findByIndexAndQuestionId(index, questionId));
    }

    @Override
    public Long persist(CreateAnswerOptionPort.Param param) {
        return repository.save(AnswerOptionMapper.mapToJpaEntity(param)).getId();
    }

}
