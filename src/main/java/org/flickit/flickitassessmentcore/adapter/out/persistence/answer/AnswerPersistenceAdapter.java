package org.flickit.flickitassessmentcore.adapter.out.persistence.answer;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.out.answer.LoadAnswersByResultPort;
import org.flickit.flickitassessmentcore.domain.Answer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class AnswerPersistenceAdapter implements LoadAnswersByResultPort {

    private final AnswerRepository answerRepository;

    @Override
    public Set<Answer> loadAnswersByResultId(UUID resultId) {
        List<AnswerJpaEntity> answerEntities = answerRepository.findAnswersByResultId(resultId);
        return answerEntities.stream().map(AnswerMapper::mapToDomainModel).collect(Collectors.toSet());
    }
}
