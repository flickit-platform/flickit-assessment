package org.flickit.flickitassessmentcore.adapter.out.persistence.answer;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.adapter.out.persistence.assessmentresult.AssessmentResultJpaEntity;
import org.flickit.flickitassessmentcore.adapter.out.persistence.assessmentresult.AssessmentResultJpaRepository;
import org.flickit.flickitassessmentcore.application.domain.crud.PaginatedResponse;
import org.flickit.flickitassessmentcore.application.port.in.answer.GetAnswerListUseCase.AnswerListItem;
import org.flickit.flickitassessmentcore.application.port.out.LoadAnswersByAssessmentAndQuestionnaireIdPort;
import org.flickit.flickitassessmentcore.application.port.out.answer.CreateAnswerPort;
import org.flickit.flickitassessmentcore.application.port.out.answer.LoadAnswerIdAndOptionIdByAssessmentResultAndQuestionPort;
import org.flickit.flickitassessmentcore.application.port.out.answer.UpdateAnswerOptionPort;
import org.flickit.flickitassessmentcore.application.service.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.SUBMIT_ANSWER_ASSESSMENT_RESULT_ID_NOT_FOUND;


@Component
@RequiredArgsConstructor
public class AnswerPersistenceJpaAdaptor implements
    CreateAnswerPort,
    UpdateAnswerOptionPort,
    LoadAnswerIdAndOptionIdByAssessmentResultAndQuestionPort,
    LoadAnswersByAssessmentAndQuestionnaireIdPort {

    private final AnswerJpaRepository repository;

    private final AssessmentResultJpaRepository assessmentResultRepository;

    @Override
    public UUID persist(CreateAnswerPort.Param param) {
        AnswerJpaEntity unsavedEntity = AnswerMapper.mapCreateParamToJpaEntity(param);
        AssessmentResultJpaEntity assessmentResult = assessmentResultRepository.findById(param.assessmentResultId())
            .orElseThrow(() -> new ResourceNotFoundException(SUBMIT_ANSWER_ASSESSMENT_RESULT_ID_NOT_FOUND));
        unsavedEntity.setAssessmentResult(assessmentResult);
        AnswerJpaEntity entity = repository.save(unsavedEntity);
        return entity.getId();
    }

    @Override
    public void updateAnswerOptionById(UpdateAnswerOptionPort.Param param) {
        repository.updateAnswerOptionById(param.id(), param.answerOptionId());
    }

    @Override
    public Optional<LoadAnswerIdAndOptionIdByAssessmentResultAndQuestionPort.Result> loadAnswerIdAndOptionId(UUID assessmentResultId, Long questionId) {
        return repository.findByAssessmentResultIdAndQuestionId(assessmentResultId, questionId)
            .map(x -> new LoadAnswerIdAndOptionIdByAssessmentResultAndQuestionPort.Result(x.getId(), x.getAnswerOptionId()));
    }

    @Override
    public PaginatedResponse<AnswerListItem> loadAnswersByAssessmentAndQuestionnaireIdPort(LoadAnswersByAssessmentAndQuestionnaireIdPort.Param param) {
        Page<AnswerJpaEntity> pageResult = repository.findByAssessmentIdAndQuestionnaireId(param.assessmentId(),
            param.questionnaireId(),
            PageRequest.of(param.page(), param.size()));

        var items = pageResult.getContent().stream()
            .map(AnswerMapper::mapJpaEntityToAnswerItem)
            .toList();
        return new PaginatedResponse<>(
            items,
            pageResult.getNumber(),
            pageResult.getNumberOfElements(),
            null,
            null,
            (int) pageResult.getTotalElements()
        );
    }
}
