package org.flickit.assessment.kit.adapter.out.persistence.questionnaire;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.kit.questionnaire.QuestionnaireJpaRepository;
import org.flickit.assessment.kit.application.domain.Questionnaire;
import org.flickit.assessment.kit.application.port.out.questionnaire.BatchUpdateQuestionnairePort;
import org.flickit.assessment.kit.application.port.out.questionnaire.CreateQuestionnairePort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class QuestionnairePersistenceJpaAdapter implements
    CreateQuestionnairePort,
    BatchUpdateQuestionnairePort {

    private final QuestionnaireJpaRepository repository;

    @Override
    public Long persist(Questionnaire questionnaire, long kitId) {
        return repository.save(QuestionnaireMapper.mapToJpaEntity(questionnaire, kitId)).getId();
    }

    @Override
    public void batchUpdate(List<Questionnaire> questionnaires, Long kitId) {
        repository.saveAll(questionnaires.stream()
            .map(questionnaire -> QuestionnaireMapper.mapToJpaEntityWithId(questionnaire, kitId))
            .toList());
        repository.flush();
    }
}
