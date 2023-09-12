package org.flickit.flickitassessmentcore.adapter.out.persistence.answer;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.adapter.out.persistence.assessmentresult.AssessmentResultJpaEntity;
import org.flickit.flickitassessmentcore.adapter.out.persistence.assessmentresult.AssessmentResultJpaRepository;
import org.flickit.flickitassessmentcore.application.domain.crud.PaginatedResponse;
import org.flickit.flickitassessmentcore.application.port.in.answer.GetAnswerListUseCase.AnswerListItem;
import org.flickit.flickitassessmentcore.application.port.out.LoadAnswersByQuestionnaireIdPort;
import org.flickit.flickitassessmentcore.application.port.out.answer.CountAnswersByQuestionAndAssessmentResultPort;
import org.flickit.flickitassessmentcore.application.port.out.answer.CreateAnswerPort;
import org.flickit.flickitassessmentcore.application.port.out.answer.LoadAnswerIdAndOptionIdByAssessmentResultAndQuestionPort;
import org.flickit.flickitassessmentcore.application.port.out.answer.UpdateAnswerOptionPort;
import org.flickit.flickitassessmentcore.application.service.exception.ResourceNotFoundException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.*;


@Component
@RequiredArgsConstructor
public class AnswerPersistenceJpaAdaptor implements
    CreateAnswerPort,
    UpdateAnswerOptionPort,
    LoadAnswerIdAndOptionIdByAssessmentResultAndQuestionPort,
    LoadAnswersByQuestionnaireIdPort,
    CountAnswersByQuestionAndAssessmentResultPort {

    private final AnswerJpaRepository repository;

    private final AssessmentResultJpaRepository assessmentResultRepo;

    @Override
    public UUID persist(CreateAnswerPort.Param param) {
        AnswerJpaEntity unsavedEntity = AnswerMapper.mapCreateParamToJpaEntity(param);
        AssessmentResultJpaEntity assessmentResult = assessmentResultRepo.findById(param.assessmentResultId())
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
    public PaginatedResponse<AnswerListItem> loadAnswersByQuestionnaireId(LoadAnswersByQuestionnaireIdPort.Param param) {
        var assessmentResult = assessmentResultRepo.findFirstByAssessment_IdOrderByLastModificationTimeDesc(param.assessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(GET_ANSWER_LIST_ASSESSMENT_RESULT_ID_NOT_FOUND));

        var pageResult = repository.findByAssessmentResultIdAndQuestionnaireIdOrderByQuestionIdAsc(assessmentResult.getId(),
            param.questionnaireId(),
            PageRequest.of(param.page(), param.size()));

        var items = pageResult.getContent().stream()
            .map(AnswerMapper::mapJpaEntityToAnswerItem)
            .toList();
        return new PaginatedResponse<>(
            items,
            pageResult.getNumber(),
            pageResult.getSize(),
            AnswerJpaEntity.Fields.QUESTION_ID,
            Sort.Direction.ASC.name().toLowerCase(),
            (int) pageResult.getTotalElements()
        );
    }

    @Override
    public int countAnswersByQuestionIdAndAssessmentResult(List<Long> questionIds, UUID resultId) {
        return repository.getCountByQuestionIdAndAssessmentResultId(questionIds, resultId);
    }
}
