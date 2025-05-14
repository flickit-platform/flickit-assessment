package org.flickit.assessment.core.application.service.attribute;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.crud.Order;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.port.in.attribute.GetAssessmentAttributeMeasuresUseCase;
import org.flickit.assessment.core.application.port.in.attribute.GetAssessmentAttributeMeasuresUseCase.Param.Sort;
import org.flickit.assessment.core.application.port.out.attribute.LoadAttributeQuestionsPort;
import org.flickit.assessment.core.application.service.measure.CalculateMeasureHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_ATTRIBUTE_MEASURES;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetAssessmentAttributeMeasuresService implements GetAssessmentAttributeMeasuresUseCase {

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final LoadAttributeQuestionsPort loadAttributeQuestionsPort;
    private final CalculateMeasureHelper calculateMeasureHelper;

    @Override
    public Result getAssessmentAttributeMeasures(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ATTRIBUTE_MEASURES))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var attributeQuestions = loadAttributeQuestionsPort.loadApplicableQuestions(param.getAssessmentId(), param.getAttributeId());

        var questionsDto = attributeQuestions.stream()
            .map(r -> new CalculateMeasureHelper.QuestionDto(
                r.question().getId(),
                r.question().getAvgWeight(param.getAttributeId()),
                r.question().getMeasure().getId(),
                r.answer()))
            .toList();

        var measures = calculateMeasureHelper.calculateMeasures(param.getAssessmentId(), questionsDto);

        var resultMeasures = measures.stream()
            .map(this::mapToResultMeasure)
            .sorted((m1, m2) -> createComparator(m1, m2, param))
            .toList();

        return new Result(resultMeasures);
    }

    private Result.Measure mapToResultMeasure(CalculateMeasureHelper.MeasureDto measureDto) {
        return new Result.Measure(measureDto.title(),
            measureDto.impactPercentage(),
            measureDto.maxPossibleScore(),
            measureDto.gainedScore(),
            measureDto.missedScore(),
            measureDto.gainedScorePercentage(),
            measureDto.missedScorePercentage());
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
