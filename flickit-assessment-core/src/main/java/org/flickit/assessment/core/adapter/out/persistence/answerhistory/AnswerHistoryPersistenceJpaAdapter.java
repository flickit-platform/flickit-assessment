package org.flickit.assessment.core.adapter.out.persistence.answerhistory;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.port.out.answerhistory.CreateAnswerHistoryPort;
import org.flickit.assessment.data.jpa.core.answer.AnswerJpaRepository;
import org.flickit.assessment.data.jpa.core.answerhistory.AnswerHistoryJpaEntity;
import org.flickit.assessment.data.jpa.core.answerhistory.AnswerHistoryJpaRepository;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static org.flickit.assessment.core.adapter.out.persistence.answerhistory.AnswerHistoryMapper.mapCreateParamToJpaEntity;
import static org.flickit.assessment.core.common.ErrorMessageKey.SUBMIT_ANSWER_ANSWER_ID_NOT_FOUND;
import static org.flickit.assessment.core.common.ErrorMessageKey.SUBMIT_ANSWER_ASSESSMENT_RESULT_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class AnswerHistoryPersistenceJpaAdapter implements
    CreateAnswerHistoryPort {

    private final AnswerHistoryJpaRepository repository;
    private final AnswerJpaRepository answerRepository;
    private final AssessmentResultJpaRepository assessmentResultRepository;

    @Override
    public UUID persist(Param param) {
        var assessmentResult = assessmentResultRepository.findById(param.assessmentResultId())
            .orElseThrow(() -> new ResourceNotFoundException(SUBMIT_ANSWER_ASSESSMENT_RESULT_NOT_FOUND));
        var answer = answerRepository.findById(param.answerId())
            .orElseThrow(() -> new ResourceNotFoundException(SUBMIT_ANSWER_ANSWER_ID_NOT_FOUND));

        AnswerHistoryJpaEntity savedEntity = repository.save(mapCreateParamToJpaEntity(param, assessmentResult, answer));
        return savedEntity.getId();
    }
}
