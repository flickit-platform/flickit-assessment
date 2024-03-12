package org.flickit.assessment.core.adapter.out.persistence.kit.question;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.domain.Question;
import org.flickit.assessment.core.application.port.out.question.LoadQuestionPort;
import org.flickit.assessment.core.application.port.out.question.LoadQuestionsBySubjectPort;
import org.flickit.assessment.data.jpa.kit.question.QuestionJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component("coreQuestionPersistenceJpaAdapter")
@RequiredArgsConstructor
public class QuestionPersistenceJpaAdapter implements
    LoadQuestionsBySubjectPort,
    LoadQuestionPort {

    private final QuestionJpaRepository repository;

    @Override
    public List<Question> loadQuestionsBySubject(long subjectId) {
        return repository.findBySubjectId(subjectId).stream()
            .map(q -> QuestionMapper.mapToDomainModel(q.getId(), null))
            .toList();
    }

    @Override
    public Optional<Question> loadByRefNum(long kitVersionId, UUID refNum) {
        return repository.findByKitVersionIdAndRefNum(kitVersionId, refNum)
            .map(QuestionMapper::mapToDomainModel);
    }
}
