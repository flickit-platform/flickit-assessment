package org.flickit.assessment.advice.adapter.out.persistence.question;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.advice.adapter.out.persistence.attribute.AttributeMapper;
import org.flickit.assessment.advice.adapter.out.persistence.option.OptionMapper;
import org.flickit.assessment.advice.application.domain.advice.QuestionnaireListItem;
import org.flickit.assessment.advice.application.port.out.question.LoadAdviceImpactfulQuestionsPort;
import org.flickit.assessment.data.jpa.kit.question.advice.AttributeAdviceView;
import org.flickit.assessment.data.jpa.kit.question.advice.OptionAdviceView;
import org.flickit.assessment.data.jpa.kit.question.advice.QuestionAdviceView;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class QuestionMapper {

    public static LoadAdviceImpactfulQuestionsPort.Result mapToListItem(List<QuestionAdviceView> questionAdviceViews) {
        var view = questionAdviceViews.get(0);

        var options = questionAdviceViews.stream()
            .map(QuestionAdviceView::getOption)
            .filter(distinctByKey(OptionAdviceView::getIndex))
            .map(OptionMapper::mapToListItem)
            .toList();

        var attributes = questionAdviceViews.stream()
            .map(QuestionAdviceView::getAttribute)
            .filter(distinctByKey(AttributeAdviceView::getId))
            .map(AttributeMapper::mapToListItem)
            .toList();

        var questionnaire = new QuestionnaireListItem(view.getQuestionnaire().getId(), view.getQuestionnaire().getTitle());

        return new LoadAdviceImpactfulQuestionsPort.Result(
            view.getId(),
            view.getTitle(),
            view.getIndex(),
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
