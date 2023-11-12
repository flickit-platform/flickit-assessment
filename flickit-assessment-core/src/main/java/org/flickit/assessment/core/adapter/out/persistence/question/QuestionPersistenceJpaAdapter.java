package org.flickit.assessment.core.adapter.out.persistence.question;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.port.out.question.LoadAssessmentKitQuestionModelsByQuestionnairePort;
import org.flickit.assessment.data.jpa.question.QuestionJpaRepository;
import org.flickit.assessment.kit.domain.Question;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class QuestionPersistenceJpaAdapter implements LoadAssessmentKitQuestionModelsByQuestionnairePort {

    private final QuestionJpaRepository repository;

    @Override
    public List<Question> load(long questionnaireId) {
        var questionJpaEntities = repository.loadByQuestionnaireId(questionnaireId);
        return questionJpaEntities.stream()
            .map(QuestionMapper::mapToKitDomainModel)
            .toList();
    }
}
