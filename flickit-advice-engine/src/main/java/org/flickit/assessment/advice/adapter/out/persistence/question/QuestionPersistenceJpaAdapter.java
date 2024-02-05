package org.flickit.assessment.advice.adapter.out.persistence.question;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.advice.application.port.out.question.LoadAdviceImpactfulQuestionsPort;
import org.flickit.assessment.data.jpa.kit.question.QuestionJpaRepository;
import org.flickit.assessment.data.jpa.kit.question.advice.QuestionAdviceView;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component("adviceQuestionPersistenceJpaAdapter")
@RequiredArgsConstructor
public class QuestionPersistenceJpaAdapter implements
    LoadAdviceImpactfulQuestionsPort {

    private final QuestionJpaRepository repository;

    @Override
    public List<Result> loadQuestions(List<Long> questionIds) {
        var questionViews = repository.findAdviceQuestions(questionIds);

        return questionViews.stream()
            .collect(Collectors.groupingBy(QuestionAdviceView::getId))
            .values().stream()
            .map(QuestionMapper::mapToListItem)
            .toList();
    }
}
