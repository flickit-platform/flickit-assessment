package org.flickit.assessment.core.application.service.attribute;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.crud.Order;
import org.flickit.assessment.common.error.ErrorMessageKey;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.common.util.MathUtils;
import org.flickit.assessment.core.application.domain.Answer;
import org.flickit.assessment.core.application.domain.Measure;
import org.flickit.assessment.core.application.port.in.attribute.GetAssessmentAttributeMeasuresUseCase;
import org.flickit.assessment.core.application.port.in.attribute.GetAssessmentAttributeMeasuresUseCase.Param.Sort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.attribute.LoadAttributeQuestionsPort;
import org.flickit.assessment.core.application.port.out.measure.LoadMeasuresPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Function;

import static java.util.stream.Collectors.*;
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_ATTRIBUTE_MEASURES;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetAssessmentAttributeMeasuresService implements GetAssessmentAttributeMeasuresUseCase {

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final LoadMeasuresPort loadMeasuresPort;
    private final LoadAttributeQuestionsPort loadAttributeQuestionsPort;

    @Override
    public Result getAssessmentAttributeMeasures(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ATTRIBUTE_MEASURES))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(ErrorMessageKey.COMMON_ASSESSMENT_RESULT_NOT_FOUND));

        var attributeQuestions = loadAttributeQuestionsPort.loadApplicableQuestions(param.getAssessmentId(), param.getAttributeId());

        var questionsDto = attributeQuestions.stream()
            .map(r -> new QuestionDto(
                r.question().getId(),
                r.question().getAvgWeight(),
                r.question().getMeasure().getId(),
                r.answer()))
            .toList();

        var attributeMaxPossibleScore = questionsDto.stream()
            .mapToDouble(QuestionDto::weight)
            .sum();

        var measureIdToQuestions = questionsDto.stream()
            .collect(groupingBy(QuestionDto::measureId));

        var measureIdToMaxPossibleScore = questionsDto.stream()
            .collect(groupingBy(
                QuestionDto::measureId,
                summingDouble(QuestionDto::weight) // Sum weights for each measureId
            ));

        var measureIds = measureIdToQuestions.keySet().stream()
            .toList();

        var idToMeasureMap = loadMeasuresPort.loadAll(measureIds, assessmentResult.getKitVersionId())
            .stream().collect(toMap(Measure::getId, Function.identity()));

        var resultMeasures = measureIdToQuestions.entrySet().stream()
            .map(entry -> buildMeasure(
                idToMeasureMap.get(entry.getKey()),
                entry.getValue(),
                measureIdToMaxPossibleScore.get(entry.getKey()),
                attributeMaxPossibleScore))
            .sorted((m1, m2) -> createComparator(m1, m2, param))
            .toList();

        return new Result(resultMeasures);
    }

    private Result.Measure buildMeasure(Measure measure,
                                        List<QuestionDto> questions,
                                        double measureMaxPossibleScore,
                                        double attributeMaxPossibleScore) {
        var impactPercentage = attributeMaxPossibleScore != 0
            ? (measureMaxPossibleScore / attributeMaxPossibleScore) * 100
            : 0.0;

        var gainedScore = questions.stream()
            .mapToDouble(q -> (q.answer() != null && q.answer().getSelectedOption() != null)
                ? q.answer().getSelectedOption().getValue() * q.weight()
                : 0.0)
            .sum();

        var missedScore = measureMaxPossibleScore - gainedScore;

        return new Result.Measure(measure.getTitle(),
            MathUtils.round(impactPercentage, 2),
            measureMaxPossibleScore,
            MathUtils.round(gainedScore, 2),
            MathUtils.round(missedScore, 2),
            MathUtils.round((gainedScore / attributeMaxPossibleScore) * 100, 2),
            MathUtils.round((missedScore / attributeMaxPossibleScore) * 100, 2)
        );
    }

    record QuestionDto(long id, double weight, long measureId, Answer answer) {
    }

    private int createComparator(Result.Measure m1, Result.Measure m2, Param param) {
        var order = Order.DESC.equals(Order.valueOf(param.getOrder())) ? -1 : 1;
        return order * switch (Sort.valueOf(param.getSort())) {
            case Sort.IMPACT_PERCENTAGE -> m1.impactPercentage().compareTo(m2.impactPercentage());
            case Sort.GAINED_SCORE -> m1.gainedScore().compareTo(m2.gainedScore());
            case Sort.MISSED_SCORE -> m1.missedScore().compareTo(m2.missedScore());
        };
    }
}
