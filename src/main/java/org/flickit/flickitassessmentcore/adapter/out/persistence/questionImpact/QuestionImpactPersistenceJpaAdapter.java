package org.flickit.flickitassessmentcore.adapter.out.persistence.questionImpact;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.out.questionImpact.LoadQuestionImpactPort;
import org.flickit.flickitassessmentcore.domain.QuestionImpact;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class QuestionImpactPersistenceJpaAdapter implements LoadQuestionImpactPort {

    @Override
    public QuestionImpact loadQuestionImpact(Long id) {
        return null;
    }
}
