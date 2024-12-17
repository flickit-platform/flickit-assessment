package org.flickit.assessment.core.adapter.out.persistence.kit.answeroption;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AnswerOption;
import org.flickit.assessment.core.application.port.out.answeroption.LoadAnswerOptionsByQuestionPort;
import org.flickit.assessment.data.jpa.kit.answeroption.AnswerOptionJpaRepository;
import org.flickit.assessment.data.jpa.kit.question.QuestionJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.flickit.assessment.core.common.ErrorMessageKey.QUESTION_ID_NOT_FOUND;

@Component("coreAnswerOptionPersistenceJpaAdapter")
@RequiredArgsConstructor
public class AnswerOptionPersistenceJpaAdapter implements LoadAnswerOptionsByQuestionPort {

    private final AnswerOptionJpaRepository repository;
    private final QuestionJpaRepository questionRepository;

    @Override
    public List<AnswerOption> loadByQuestionId(Long questionId, Long kitVersionId) {
        var question = questionRepository.findByIdAndKitVersionId(questionId, kitVersionId)
            .orElseThrow(() -> new ResourceNotFoundException(QUESTION_ID_NOT_FOUND));

        return repository.findAllByAnswerRangeIdAndKitVersionIdOrderByIndex(question.getAnswerRangeId(), kitVersionId).stream()
            .map(AnswerOptionMapper::mapToDomainModel)
            .toList();
    }
}
