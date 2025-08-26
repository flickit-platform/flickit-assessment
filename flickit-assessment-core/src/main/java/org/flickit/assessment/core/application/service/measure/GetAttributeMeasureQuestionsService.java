package org.flickit.assessment.core.application.service.measure;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.port.in.measure.GetAttributeMeasureQuestionsUseCase;
import org.flickit.assessment.core.application.port.out.attribute.LoadAttributeQuestionsPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_ATTRIBUTE_MEASURE_QUESTIONS;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetAttributeMeasureQuestionsService implements GetAttributeMeasureQuestionsUseCase {

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final LoadAttributeQuestionsPort loadAttributeQuestionsPort;

    @Override
    public Result getQuestions(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ATTRIBUTE_MEASURE_QUESTIONS))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var portResults = loadAttributeQuestionsPort.loadApplicableMeasureQuestions(
            param.getAssessmentId(),
            param.getAttributeId(),
            param.getMeasureId());

        var measureQuestions = portResults.stream()
            .map(item -> toMeasureQuestions(item, param.getAttributeId()))
            .toList();

        return new Result(measureQuestions);
    }

    private MeasureQuestion toMeasureQuestions(LoadAttributeQuestionsPort.Result item, long attributeId) {
        var question = item.question();
        var answer = item.answer();

        double weight = question.getAvgWeight(attributeId);
        Integer optionIndex = null;
        String optionTitle = null;
        double gained = 0.0;
        double missed = weight;

        if (answer != null && answer.getSelectedOption() != null) {
            optionIndex = answer.getSelectedOption().getIndex();
            optionTitle = answer.getSelectedOption().getTitle();
            gained = answer.getSelectedOption().getValue() * weight;
            missed = weight - gained;
        }

        return new MeasureQuestion(
            new MeasureQuestion.Question(question.getId(), question.getIndex(), question.getTitle()),
            new MeasureQuestion.Answer(optionIndex, optionTitle, gained, missed)
        );
    }
}
