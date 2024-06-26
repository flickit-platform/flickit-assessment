package org.flickit.assessment.advice.adapter.out.calculation;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.advice.application.domain.advice.AdviceAttribute;
import org.flickit.assessment.advice.application.domain.advice.AdviceOption;
import org.flickit.assessment.advice.application.domain.advice.AdviceQuestion;
import org.flickit.assessment.advice.application.domain.advice.AdviceQuestionnaire;
import org.flickit.assessment.advice.application.port.out.calculation.LoadCreatedAdviceDetailsPort;
import org.flickit.assessment.data.jpa.kit.question.QuestionJpaRepository;
import org.flickit.assessment.data.jpa.kit.question.advice.AttributeAdviceView;
import org.flickit.assessment.data.jpa.kit.question.advice.OptionAdviceView;
import org.flickit.assessment.data.jpa.kit.question.advice.QuestionAdviceView;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.stream.Collectors.groupingBy;

@Component
@RequiredArgsConstructor
public class LoadCreatedAdviceDetailsAdapter implements
    LoadCreatedAdviceDetailsPort {

    private final QuestionJpaRepository repository;

    @Override
    public List<Result> loadAdviceDetails(List<Long> questionIds, Long kitVersionId) {
        var questionViews = repository.findAdviceQuestionsDetail(questionIds, kitVersionId);

        return questionViews.stream()
            .collect(groupingBy(QuestionAdviceView::getId))
            .values().stream()
            .map(this::mapToAdviceListItem)
            .toList();
    }

    private LoadCreatedAdviceDetailsPort.Result mapToAdviceListItem(List<QuestionAdviceView> questionAdviceViews) {
        var view = questionAdviceViews.get(0);
        AdviceQuestion question = new AdviceQuestion(view.getId(), view.getTitle(), view.getIndex());

        var options = questionAdviceViews.stream()
            .map(QuestionAdviceView::getOption)
            .filter(distinctByKey(OptionAdviceView::getIndex))
            .map(op -> new AdviceOption(op.getIndex(), op.getTitle()))
            .toList();

        var attributes = questionAdviceViews.stream()
            .map(QuestionAdviceView::getAttribute)
            .filter(distinctByKey(AttributeAdviceView::getId))
            .map(att -> new AdviceAttribute(att.getId(), att.getTitle()))
            .toList();

        var questionnaire = new AdviceQuestionnaire(view.getQuestionnaire().getId(), view.getQuestionnaire().getTitle());

        return new LoadCreatedAdviceDetailsPort.Result(
            question,
            options,
            attributes,
            questionnaire
        );
    }

    private static <T> Predicate<T> distinctByKey(
        Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }
}
