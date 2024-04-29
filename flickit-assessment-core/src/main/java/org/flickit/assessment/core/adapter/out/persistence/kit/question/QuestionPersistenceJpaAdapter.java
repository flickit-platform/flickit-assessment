package org.flickit.assessment.core.adapter.out.persistence.kit.question;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.core.adapter.out.persistence.answeroption.AnswerOptionMapper;
import org.flickit.assessment.core.application.domain.AnswerOption;
import org.flickit.assessment.core.application.domain.Question;
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
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;

@Component("coreQuestionPersistenceJpaAdapter")
@RequiredArgsConstructor
public class QuestionPersistenceJpaAdapter implements
    LoadQuestionsBySubjectPort,
    LoadQuestionnaireQuestionListPort {

    private final QuestionJpaRepository repository;
    private final AnswerOptionJpaRepository answerOptionRepository;

    @Override
    public List<Question> loadQuestionsBySubject(long subjectId) {
        return repository.findBySubjectId(subjectId).stream()
            .map(q -> QuestionMapper.mapToDomainModel(q.getId(), null))
            .toList();
    }

    @Override
    public PaginatedResponse<Question> loadByQuestionnaireId(Long questionnaireId, int size, int page) {
        var pageResult = repository.findAllByQuestionnaireIdOrderByIndex(questionnaireId, PageRequest.of(page, size));
        List<Long> ids = pageResult.getContent().stream()
            .map(QuestionJpaEntity::getId)
            .toList();
        Map<Long, List<AnswerOptionJpaEntity>> questionIdToAnswerOptionsMap = answerOptionRepository.findAllByQuestionIdInOrderByQuestionIdIndex(ids).stream()
            .collect(groupingBy(AnswerOptionJpaEntity::getQuestionId));

        var items = pageResult.getContent().stream()
            .map(q -> {
                List<AnswerOption> answerOptions = questionIdToAnswerOptionsMap.get(q.getId()).stream()
                    .map(AnswerOptionMapper::mapToDomainModel)
                    .toList();
                Question question = QuestionMapper.mapToDomainModel(q);
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
}
