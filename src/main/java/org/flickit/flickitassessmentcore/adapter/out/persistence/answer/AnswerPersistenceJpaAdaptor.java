package org.flickit.flickitassessmentcore.adapter.out.persistence.answer;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.adapter.out.persistence.assessmentresult.AssessmentResultJpaEntity;
import org.flickit.flickitassessmentcore.adapter.out.persistence.assessmentresult.AssessmentResultJpaRepository;
import org.flickit.flickitassessmentcore.application.port.out.answer.CheckAnswerExistenceByAssessmentResultIdAndQuestionIdPort;
import org.flickit.flickitassessmentcore.application.port.out.answer.LoadAnswerByAssessmentResultIdAndQuestionIdPort;
import org.flickit.flickitassessmentcore.application.port.out.answer.SaveAnswerPort;
import org.flickit.flickitassessmentcore.application.port.out.answer.UpdateAnswerPort;
import org.flickit.flickitassessmentcore.application.service.exception.ResourceNotFoundException;
import org.flickit.flickitassessmentcore.domain.Answer;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.SUBMIT_ANSWER_ASSESSMENT_RESULT_ID_NOT_FOUND_MESSAGE;


@Component
@RequiredArgsConstructor
public class AnswerPersistenceJpaAdaptor implements
    SaveAnswerPort,
    UpdateAnswerPort,
    CheckAnswerExistenceByAssessmentResultIdAndQuestionIdPort,
    LoadAnswerByAssessmentResultIdAndQuestionIdPort {

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
    public UUID update(UpdateAnswerPort.Param param) {
        AnswerJpaEntity unsavedEntity = AnswerMapper.mapUpdateParamToJpaEntity(param);
        AssessmentResultJpaEntity assessmentResult = assessmentResultRepository.findById(param.assessmentResultId())
            .orElseThrow(() -> new ResourceNotFoundException(SUBMIT_ANSWER_ASSESSMENT_RESULT_ID_NOT_FOUND_MESSAGE));
        unsavedEntity.setAssessmentResult(assessmentResult);
        AnswerJpaEntity entity = repository.save(unsavedEntity);
        return entity.getId();
    }

    @Override
    public boolean existsByAssessmentResultIdAndQuestionId(UUID assessmentResultId, Long questionId) {
        return repository.existsByAssessmentResult_IdAndQuestionId(assessmentResultId, questionId);
    }

    @Override
    public Answer loadByAssessmentResultIdAndQuestionId(UUID assessmentResultId, Long questionId) {
        AnswerJpaEntity answerEntity = repository.findByAssessmentResult_IdAndQuestionId(assessmentResultId, questionId);
        return AnswerMapper.mapToDomainModel(answerEntity);
    }
}
