package org.flickit.assessment.kit.adapter.out.persistence.question;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.kit.question.QuestionJpaRepository;
import org.flickit.assessment.kit.application.domain.Question;
import org.flickit.assessment.kit.application.port.out.question.LoadQuestionsByKitPort;
import org.flickit.assessment.kit.application.port.out.question.UpdateQuestionPort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class QuestionPersistenceJpaAdapter implements
    LoadQuestionsByKitPort,
    UpdateQuestionPort {

    private final QuestionJpaRepository repository;

    @Override
    public List<Question> loadByKit(Long kitId) {
        return repository.findByKitId(kitId).stream()
            .map(QuestionMapper::mapToDomainModel)
            .toList();
    }

    @Override
    public void update(Param param) {
        repository.update(param.id(), param.title(), param.description(), param.index(), param.isNotApplicable());
    }
}
