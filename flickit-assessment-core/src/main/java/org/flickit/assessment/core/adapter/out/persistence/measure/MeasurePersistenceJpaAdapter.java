package org.flickit.assessment.core.adapter.out.persistence.measure;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.port.out.measure.LoadMeasureScoresPort;
import org.flickit.assessment.data.jpa.kit.measure.MeasureJpaEntity;
import org.flickit.assessment.data.jpa.kit.measure.MeasureJpaRepository;
import org.flickit.assessment.data.jpa.kit.question.QuestionJoinQuestionImpactView;
import org.flickit.assessment.data.jpa.kit.question.QuestionJpaRepository;
import org.flickit.assessment.data.jpa.kit.questionimpact.QuestionImpactJpaEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Component("coreMeasurePersistenceJpaAdapter")
@RequiredArgsConstructor
public class MeasurePersistenceJpaAdapter implements
    LoadMeasureScoresPort {

    private final MeasureJpaRepository repository;
    private final QuestionJpaRepository questionJpaRepository;

    @Override
    public List<Result> loadAll(List<Long> measureIds, long kitVersionId) {
        var idToEntityMap = repository.findAllByIdInAndKitVersionId(measureIds, kitVersionId).stream()
            .collect(Collectors.toMap(MeasureJpaEntity::getId, Function.identity()));
        var questionsWithImpacts = questionJpaRepository.findAllByKitVersionIdAndMeasureId(kitVersionId, measureIds);

        return questionsWithImpacts.stream()
            .collect(groupingBy(v -> v.getQuestion().getMeasureId()))
            .entrySet().stream()
            .map(v -> mapToResult(v.getKey(), idToEntityMap.get(v.getKey()), v.getValue()))
            .toList();
    }

    private LoadMeasureScoresPort.Result mapToResult(Long measureId, MeasureJpaEntity entity, List<QuestionJoinQuestionImpactView> views) {
        double measureScore = views.stream()
            .map(QuestionJoinQuestionImpactView::getQuestionImpact)
            .collect(groupingBy(QuestionImpactJpaEntity::getQuestionId))
            .values().stream()
            .mapToDouble(qi -> qi.stream()
                .mapToDouble(QuestionImpactJpaEntity::getWeight)
                .average()
                .orElse(0)
            ).sum();

        return new LoadMeasureScoresPort.Result(measureId, entity.getTitle(), measureScore);
    }
}
