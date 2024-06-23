package org.flickit.assessment.core.adapter.out.persistence.kit.question;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.adapter.out.persistence.answeroption.AnswerOptionMapper;
import org.flickit.assessment.core.application.domain.AnswerOption;
import org.flickit.assessment.core.application.domain.Question;
import org.flickit.assessment.core.application.port.out.question.LoadQuestionMayNotBeApplicablePort;
import org.flickit.assessment.core.application.port.out.question.LoadQuestionnaireQuestionListPort;
import org.flickit.assessment.core.application.port.out.question.LoadQuestionsBySubjectPort;
import org.flickit.assessment.data.jpa.kit.answeroption.AnswerOptionJpaEntity;
import org.flickit.assessment.data.jpa.kit.answeroption.AnswerOptionJpaRepository;
import org.flickit.assessment.data.jpa.kit.question.QuestionJpaEntity;
import org.flickit.assessment.data.jpa.kit.question.QuestionJpaRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.stream.Collectors.groupingBy;
import static org.flickit.assessment.core.common.ErrorMessageKey.SUBMIT_ANSWER_QUESTION_ID_NOT_FOUND;

@Component("coreQuestionPersistenceJpaAdapter")
@RequiredArgsConstructor
public class QuestionPersistenceJpaAdapter implements
    LoadQuestionsBySubjectPort,
    LoadQuestionnaireQuestionListPort,
    LoadQuestionMayNotBeApplicablePort {

    private final QuestionJpaRepository repository;
    private final AnswerOptionJpaRepository answerOptionRepository;

    @Override
    public List<Question> loadQuestionsBySubject(long subjectId, long kitVersionId) {
        return repository.findBySubjectId(subjectId, kitVersionId).stream()
            .map(q -> QuestionMapper.mapToDomainModel(q.getId(), null))
            .toList();
    }

    @Override
    public PaginatedResponse<Question> loadByQuestionnaireId(Long questionnaireId, Long kitVersionId, int size, int page) {
        var pageResult = repository.findAllByQuestionnaireIdAndKitVersionIdOrderByIndex(questionnaireId, kitVersionId, PageRequest.of(page, size));
        List<Long> ids = pageResult.getContent().stream()
            .map(QuestionJpaEntity::getId)
            .toList();
        var questionIdToAnswerOptionsMap = answerOptionRepository.findAllByQuestionIdInAndKitVersionIdOrderByQuestionIdIndex(ids, kitVersionId).stream()
            .collect(groupingBy(AnswerOptionJpaEntity::getQuestionId));

        var items = pageResult.getContent().stream()
            .map(q -> {
                List<AnswerOption> answerOptions = questionIdToAnswerOptionsMap.get(q.getId()).stream()
                    .map(e -> AnswerOptionMapper.mapToDomainModel(e, null))
                    .toList();
                Question question = QuestionMapper.mapToDomainModel(q, null);
                question.setOptions(answerOptions);
                return question;
            })
            .toList();

        return new PaginatedResponse<>(
            items,
            pageResult.getNumber(),
            pageResult.getSize(),
            QuestionJpaEntity.Fields.INDEX,
            Sort.Direction.ASC.name().toLowerCase(),
            (int) pageResult.getTotalElements()
        );
    }

    @Override
    public boolean loadMayNotBeApplicableById(Long id, long kitVersionId) {
        return repository.findByIdAndKitVersionId(id, kitVersionId)
            .orElseThrow(() -> new ResourceNotFoundException(SUBMIT_ANSWER_QUESTION_ID_NOT_FOUND))
            .getMayNotBeApplicable();
    }
}
