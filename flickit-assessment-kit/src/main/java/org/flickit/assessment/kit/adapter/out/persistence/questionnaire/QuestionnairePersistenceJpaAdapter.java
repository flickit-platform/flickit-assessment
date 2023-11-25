package org.flickit.assessment.kit.adapter.out.persistence.questionnaire;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.kit.questionnaire.QuestionnaireJpaRepository;
import org.flickit.assessment.kit.application.domain.Questionnaire;
import org.flickit.assessment.kit.application.port.out.questionnaire.LoadQuestionnairesByKitPort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class QuestionnairePersistenceJpaAdapter implements LoadQuestionnairesByKitPort {

    private final QuestionnaireJpaRepository repository;

    @Override
    public List<Questionnaire> loadByKitId(Long kitId) {
        return repository.findAllByAssessmentKitId(kitId).stream()
            .map(QuestionnaireMapper::mapToDomainModel)
            .toList();
    }
}
