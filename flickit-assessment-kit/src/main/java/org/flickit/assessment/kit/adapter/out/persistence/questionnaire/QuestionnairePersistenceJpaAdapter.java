package org.flickit.assessment.kit.adapter.out.persistence.questionnaire;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.kit.questionnaire.QuestionnaireJpaRepository;
import org.flickit.assessment.kit.application.domain.Questionnaire;
import org.flickit.assessment.kit.application.port.out.questionnaire.CreateQuestionnairePort;
import org.flickit.assessment.kit.application.port.out.questionnaire.LoadQuestionnairesByKitPort;
import org.flickit.assessment.kit.application.port.out.questionnaire.UpdateQuestionnaireByKitPort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class QuestionnairePersistenceJpaAdapter implements
    LoadQuestionnairesByKitPort,
    CreateQuestionnairePort,
    UpdateQuestionnaireByKitPort {

    private final QuestionnaireJpaRepository repository;

    @Override
    public List<Questionnaire> loadByKitId(Long kitId) {
        return repository.findAllByAssessmentKitId(kitId).stream()
            .map(QuestionnaireMapper::mapToDomainModel)
            .toList();
    }

    @Override
    public Long persist(Questionnaire questionnaire, Long kitId) {
        return repository.save(QuestionnaireMapper.mapToJpaEntity(questionnaire, kitId)).getId();
    }

    @Override
    public void updateByKitId(Param param) {
        repository.updateByKitId(param.code(), param.title(), param.description(), param.index(), param.kitId());
    }
}
