package org.flickit.assessment.kit.adapter.out.persistence.questionnaire;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.kit.questionnaire.QuestionnaireJpaRepository;
import org.flickit.assessment.kit.application.domain.Questionnaire;
import org.flickit.assessment.kit.application.port.out.questionnaire.CreateQuestionnairePort;
import org.flickit.assessment.kit.application.port.out.questionnaire.UpdateQuestionnairePort;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QuestionnairePersistenceJpaAdapter implements
    CreateQuestionnairePort,
    UpdateQuestionnairePort {

    private final QuestionnaireJpaRepository repository;

    @Override
    public Long persist(Questionnaire questionnaire, long kitId) {
        return repository.save(QuestionnaireMapper.mapToJpaEntity(questionnaire, kitId)).getId();
    }

    @Override
    public void update(UpdateQuestionnairePort.Param param) {
        repository.update(param.id(), param.title(), param.index(), param.description(), param.lastModificationTime());
    }
}
