package org.flickit.assessment.kit.adapter.out.persistence.question;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.kit.question.QuestionJpaRepository;
import org.flickit.assessment.kit.application.port.out.question.UpdateQuestionPort;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QuestionPersistenceJpaAdapter implements
    UpdateQuestionPort {

    private final QuestionJpaRepository repository;

    @Override
    public void update(Param param) {
        repository.update(param.id(), param.title(), param.index(), param.description(), param.isNotApplicable());
    }

}
