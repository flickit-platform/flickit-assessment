package org.flickit.assessment.core.adapter.out.persistence.kit.question;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.Question;
import org.flickit.assessment.core.application.port.out.question.CheckQuestionMayNotBeApplicablePort;
import org.flickit.assessment.core.application.port.out.question.LoadQuestionsBySubjectPort;
import org.flickit.assessment.data.jpa.kit.question.QuestionJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.flickit.assessment.core.common.ErrorMessageKey.SUBMIT_ANSWER_QUESTION_ID_NOT_FOUND;

@Component("coreQuestionPersistenceJpaAdapter")
@RequiredArgsConstructor
public class QuestionPersistenceJpaAdapter implements
    LoadQuestionsBySubjectPort,
    CheckQuestionMayNotBeApplicablePort {

    private final QuestionJpaRepository repository;

    @Override
    public List<Question> loadQuestionsBySubject(long subjectId) {
        return repository.findBySubjectId(subjectId).stream()
            .map(q -> QuestionMapper.mapToDomainModel(q.getId(), null))
            .toList();
    }

    @Override
    public boolean checkQuestionMayNotBeApplicable(Long questionId) {
        return repository.findById(questionId)
            .orElseThrow(() -> new ResourceNotFoundException(SUBMIT_ANSWER_QUESTION_ID_NOT_FOUND))
            .getMayNotBeApplicable();
    }
}
