package org.flickit.assessment.core.application.service.answer;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.port.in.answer.SubmitAnswerUseCase;
import org.flickit.assessment.core.application.port.out.answer.CreateAnswerPort;
import org.flickit.assessment.core.application.port.out.answer.LoadAnswerPort;
import org.flickit.assessment.core.application.port.out.answer.UpdateAnswerPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.InvalidateAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

import static org.flickit.assessment.core.common.ErrorMessageKey.SUBMIT_ANSWER_ASSESSMENT_RESULT_NOT_FOUND;

@Service
@Transactional
@RequiredArgsConstructor
public class SubmitAnswerService implements SubmitAnswerUseCase {

    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final CreateAnswerPort createAnswerPort;
    private final LoadAnswerPort loadAnswerPort;
    private final UpdateAnswerPort updateAnswerPort;
    private final InvalidateAssessmentResultPort invalidateAssessmentResultPort;

    @Override
    public Result submitAnswer(Param param) {
        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(SUBMIT_ANSWER_ASSESSMENT_RESULT_NOT_FOUND));

        var loadedAnswer = loadAnswerPort.load(assessmentResult.getId(), param.getQuestionId());
        var answerOptionId = Boolean.TRUE.equals(param.getIsNotApplicable()) ? null : param.getAnswerOptionId();
        var confidenceLevelId = Boolean.TRUE.equals(param.getIsNotApplicable()) ? null : param.getConfidenceLevelId();

        if (loadedAnswer.isEmpty()) {
            return saveAnswer(param, assessmentResult.getId(), answerOptionId, confidenceLevelId);
        }

        var loadedAnswerOptionId = loadedAnswer.get().getSelectedOption() == null ? null : loadedAnswer.get().getSelectedOption().getId();

        var isNotApplicableChanged = !Objects.equals(param.getIsNotApplicable(), loadedAnswer.get().getIsNotApplicable());
        var isAnswerOptionChanged = Objects.equals(Boolean.TRUE, param.getIsNotApplicable()) ? Boolean.FALSE : !Objects.equals(answerOptionId, loadedAnswerOptionId);
        var isConfidenceLevelChanged = Objects.equals(Boolean.TRUE, param.getIsNotApplicable()) ? Boolean.FALSE : !Objects.equals(confidenceLevelId, loadedAnswer.get().getConfidenceLevelId());

        if (isNotApplicableChanged || isAnswerOptionChanged || isConfidenceLevelChanged) {
            var updateParam = toUpdateAnswerParam(loadedAnswer.get().getId(), answerOptionId, confidenceLevelId, param.getIsNotApplicable());
            var isCalculateValid = !isAnswerOptionChanged && !isNotApplicableChanged;
            updateAnswer(assessmentResult.getId(), updateParam, isCalculateValid, !isConfidenceLevelChanged);
        }

        return new Result(loadedAnswer.get().getId());
    }

    private Result saveAnswer(Param param, UUID assessmentResultId, Long answerOptionId, Integer confidenceLevelId) {
        UUID savedAnswerId = createAnswerPort.persist(toCreateParam(param, assessmentResultId, answerOptionId, confidenceLevelId));
        if (answerOptionId != null || confidenceLevelId != null || Boolean.TRUE.equals(param.getIsNotApplicable())) {
            invalidateAssessmentResultPort.invalidateById(assessmentResultId, Boolean.FALSE, Boolean.FALSE);
        }
        return new Result(savedAnswerId);
    }

    private CreateAnswerPort.Param toCreateParam(Param param, UUID assessmentResultId, Long answerOptionId, Integer confidenceLevelId) {
        return new CreateAnswerPort.Param(
            assessmentResultId,
            param.getQuestionnaireId(),
            param.getQuestionId(),
            answerOptionId,
            confidenceLevelId,
            param.getIsNotApplicable()
        );
    }

    private UpdateAnswerPort.Param toUpdateAnswerParam(UUID answerId, Long answerOptionId, Integer confidenceLevelId, Boolean isNotApplicable) {
        return new UpdateAnswerPort.Param(answerId, answerOptionId, confidenceLevelId, isNotApplicable);
    }

    private void updateAnswer(UUID assessmentResultId, UpdateAnswerPort.Param updateParam, Boolean isCalculateValid, Boolean isConfidenceValid) {
        updateAnswerPort.update(updateParam);
        invalidateAssessmentResultPort.invalidateById(assessmentResultId, isCalculateValid, isConfidenceValid);
    }

}
