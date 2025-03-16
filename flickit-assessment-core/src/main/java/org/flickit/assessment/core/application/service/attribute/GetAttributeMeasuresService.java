package org.flickit.assessment.core.application.service.attribute;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.crud.Order;
import org.flickit.assessment.common.error.ErrorMessageKey;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AttributeValue;
import org.flickit.assessment.core.application.domain.Measure;
import org.flickit.assessment.core.application.domain.Question;
import org.flickit.assessment.core.application.domain.QuestionImpact;
import org.flickit.assessment.core.application.port.in.attribute.GetAttributeMeasuresUseCase;
import org.flickit.assessment.core.application.port.in.attribute.GetAttributeMeasuresUseCase.Param.Sort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.attributevalue.LoadAttributeValuePort;
import org.flickit.assessment.core.application.port.out.measure.LoadMeasureScoresPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_ATTRIBUTE_MEASURES;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetAttributeMeasuresService implements GetAttributeMeasuresUseCase {

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final LoadAttributeValuePort loadAttributeValuePort;
    private final LoadMeasureScoresPort loadMeasureScoresPort;

    @Override
    public Result getAttributeMeasures(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ATTRIBUTE_MEASURES))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(ErrorMessageKey.COMMON_ASSESSMENT_RESULT_NOT_FOUND));

        var attributeValue = loadAttributeValuePort.load(assessmentResult.getId(), param.getAttributeId());

        var measureToQuestions = attributeValue.getAttribute().getQuestions().stream()
            .collect(groupingBy(Question::getMeasure));

        var measureIds = measureToQuestions.keySet().stream()
            .map(Measure::getId)
            .toList();

        var measureTotalScoresMap = loadMeasureScoresPort.loadAll(measureIds, assessmentResult.getKitVersionId())
            .stream().collect(toMap(LoadMeasureScoresPort.Result::id, Function.identity()));

        return new Result(measureToQuestions.entrySet().stream()
            .map(entry -> createMeasure(attributeValue, entry.getKey(), entry.getValue(), measureTotalScoresMap))
            .sorted((m1, m2) -> createComparator(m1, m2, param))
            .toList());
    }

    private Result.Measure createMeasure(AttributeValue attributeValue,
                                         Measure measure,
                                         List<Question> questions,
                                         Map<Long, LoadMeasureScoresPort.Result> measureMap) {
        var totalAttributeMeasureScore = questions.stream()
            .mapToDouble(q -> q.getImpacts().stream()
                .mapToDouble(QuestionImpact::getWeight)
                .average()
                .orElse(0))
            .sum();

        var questionIdToScoreMap = questions.stream()
            .collect(toMap(Question::getId,
                q -> q.getImpacts().stream().mapToDouble(QuestionImpact::getWeight)
                    .average()
                    .orElse(0)));

        var promisedScore = questionIdToScoreMap.values().stream()
            .mapToDouble(s -> s)
            .sum();

        var gainedScore = attributeValue.getAnswers().stream()
            .filter(a -> questionIdToScoreMap.containsKey(a.getQuestionId()))
//            TODO calculate correct value of selected option
            .mapToDouble(a -> a.getSelectedOption().getValue() * questionIdToScoreMap.get(a.getQuestionId()))
            .sum();

        var missedScore = promisedScore - gainedScore;

        double measureTotalScore = measureMap.get(measure.getId()).score();

        return new Result.Measure(measureMap.get(measure.getId()).title(),
            totalAttributeMeasureScore / measureTotalScore,
            promisedScore,
            gainedScore,
            missedScore,
            gainedScore / measureTotalScore,
            missedScore / measureTotalScore
        );
    }

    private int createComparator(Result.Measure m1, Result.Measure m2, Param param) {
        int order = Order.ASC.equals(Order.valueOf(param.getOrder())) ? 1 : -1;
        return order * switch (Sort.valueOf(param.getSort())) {
            case Sort.TITLE -> m1.title().compareTo(m2.title());
            case Sort.IMPACT_PERCENTAGE -> m1.impactPercentage().compareTo(m2.impactPercentage());
            case Sort.PROMISED_SCORE -> m1.promisedScore().compareTo(m2.promisedScore());
            case Sort.GAINED_SCORE -> m1.gainedScore().compareTo(m2.gainedScore());
            case Sort.MISSED_SCORE -> m1.missedScore().compareTo(m2.missedScore());
            case Sort.GAINED_SCORE_PERCENTAGE -> m1.gainedScorePercentage().compareTo(m2.gainedScorePercentage());
            case Sort.MISSED_SCORE_PERCENTAGE -> m1.missedScorePercentage().compareTo(m2.missedScorePercentage());
        };
    }
}
