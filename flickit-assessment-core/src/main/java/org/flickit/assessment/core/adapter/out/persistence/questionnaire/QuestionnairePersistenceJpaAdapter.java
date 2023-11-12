package org.flickit.assessment.core.adapter.out.persistence.questionnaire;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.port.out.questionnaire.LoadAssessmentKitQuestionnaireModelsByKitPort;
import org.flickit.assessment.data.jpa.questionnaire.QuestionnaireJpaRepository;
import org.flickit.assessment.kit.domain.Questionnaire;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class QuestionnairePersistenceJpaAdapter implements LoadAssessmentKitQuestionnaireModelsByKitPort {

    private final QuestionnaireJpaRepository repository;

    @Override
    public List<Questionnaire> load(Long kitId) {
        var questionnaireJpaEntities = repository.loadByAssessmentKitId(kitId);
        return questionnaireJpaEntities.stream()
            .map(QuestionnaireMapper::mapToKitDomainModel)
            .toList();
    }
}
