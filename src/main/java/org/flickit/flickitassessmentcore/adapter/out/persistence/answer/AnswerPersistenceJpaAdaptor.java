package org.flickit.flickitassessmentcore.adapter.out.persistence.answer;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.adapter.out.persistence.assessmentresult.AssessmentResultJpaEntity;
import org.flickit.flickitassessmentcore.adapter.out.persistence.assessmentresult.AssessmentResultJpaRepository;
import org.flickit.flickitassessmentcore.application.port.out.answer.*;
import org.flickit.flickitassessmentcore.application.service.exception.ResourceNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.SUBMIT_ANSWER_ASSESSMENT_RESULT_ID_NOT_FOUND_MESSAGE;


@Component
@RequiredArgsConstructor
public class AnswerPersistenceJpaAdaptor implements
    SaveAnswerPort,
    UpdateAnswerOptionPort,
    LoadSubmitAnswerExistAnswerViewByAssessmentResultAndQuestionPort,
    LoadAnswerIdAndIsApplicableByAssessmentResultAndQuestionPort,
    UpdateAnswerIsApplicablePort {

    private final AnswerJpaRepository repository;

    private final AssessmentResultJpaRepository assessmentResultRepository;

    @Override
    public UUID persist(SaveAnswerPort.Param param) {
        AnswerJpaEntity unsavedEntity = AnswerMapper.mapSaveParamToJpaEntity(param);
        AssessmentResultJpaEntity assessmentResult = assessmentResultRepository.findById(param.assessmentResultId())
            .orElseThrow(() -> new ResourceNotFoundException(SUBMIT_ANSWER_ASSESSMENT_RESULT_ID_NOT_FOUND_MESSAGE));
        unsavedEntity.setAssessmentResult(assessmentResult);
        AnswerJpaEntity entity = repository.save(unsavedEntity);
        return entity.getId();
    }

    @Override
    public void updateAnswerOptionById(UpdateAnswerOptionPort.Param param) {
        repository.updateAnswerOptionById(param.id(), param.answerOptionId());
    }

    @Override
    public Optional<LoadSubmitAnswerExistAnswerViewByAssessmentResultAndQuestionPort.Result> loadView(UUID assessmentResultId, Long questionId) {
        return repository.findByAssessmentResultIdAndQuestionId(assessmentResultId, questionId)
            .map(x -> new LoadSubmitAnswerExistAnswerViewByAssessmentResultAndQuestionPort.Result(x.getId(), x.getAnswerOptionId(), x.getIsApplicable()));
    }

    @Override
    public void updateAnswerIsApplicableAndRemoveOptionById(UpdateAnswerIsApplicablePort.Param param) {
        repository.updateIsApplicableAndRemoveOptionIdById(param.id(), param.isApplicable());
    }

    @Override
    public Optional<LoadAnswerIdAndIsApplicableByAssessmentResultAndQuestionPort.Result> loadAnswerIdAndIsApplicable(UUID assessmentResultId, Long questionId) {
        return repository.findByAssessmentResultIdAndQuestionId_(assessmentResultId, questionId)
            .map(x -> new LoadAnswerIdAndIsApplicableByAssessmentResultAndQuestionPort.Result(x.getId(), x.getIsApplicable()));
    }
}
