package org.flickit.assessment.core.application.service.measure;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.port.in.measure.GetAttributeMeasureQuestionsUseCase;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.attribute.LoadAttributeQuestionsPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_ATTRIBUTE_MEASURE_QUESTIONS;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetAttributeMeasureQuestionsService implements GetAttributeMeasureQuestionsUseCase {

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final LoadAttributeQuestionsPort loadAttributeQuestionsPort;

    @Override
    public Result getQuestions(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ATTRIBUTE_MEASURE_QUESTIONS))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(COMMON_ASSESSMENT_RESULT_NOT_FOUND));

        var portResults = loadAttributeQuestionsPort.loadAttributeMeasureQuestions(
            assessmentResult,
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

        int weight = (int) question.getAvgWeight(attributeId);
        Integer optionIndex = null;
        String optionTitle = null;
        boolean isNotApplicable = false;
        double gained = 0.0;
        double missed = weight;

        if (answer != null) {
            isNotApplicable = Boolean.TRUE.equals(answer.getIsNotApplicable());
            if (answer.getSelectedOption() != null) {
                optionIndex = answer.getSelectedOption().getIndex();
                optionTitle = answer.getSelectedOption().getTitle();
                if (!isNotApplicable) {
                    gained = answer.getSelectedOption().getValue() * weight;
                    missed = weight - gained;
                } else {
                    gained = 0.0;
                    missed = 0.0;
                }
            } else {
                if (isNotApplicable) {
                    gained = 0.0;
                    missed = 0.0;
                }
            }
        }

        return new MeasureQuestion(
            new MeasureQuestion.Question(question.getId(), question.getIndex(), question.getTitle(), weight),
            new MeasureQuestion.Answer(optionIndex, optionTitle, isNotApplicable, gained, missed)
        );
    }
}
