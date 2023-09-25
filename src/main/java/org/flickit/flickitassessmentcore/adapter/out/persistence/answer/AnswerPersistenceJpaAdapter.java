package org.flickit.flickitassessmentcore.adapter.out.persistence.answer;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.adapter.out.persistence.assessmentresult.AssessmentResultJpaEntity;
import org.flickit.flickitassessmentcore.adapter.out.persistence.assessmentresult.AssessmentResultJpaRepository;
import org.flickit.flickitassessmentcore.application.domain.crud.PaginatedResponse;
import org.flickit.flickitassessmentcore.application.port.in.answer.GetAnswerListUseCase.AnswerListItem;
import org.flickit.flickitassessmentcore.application.port.in.questionnaire.GetQuestionnairesProgressUseCase.QuestionnaireProgress;
import org.flickit.flickitassessmentcore.application.port.out.answer.*;
import org.flickit.flickitassessmentcore.application.port.out.questionnaire.GetQuestionnairesProgressPort;
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
public class AnswerPersistenceJpaAdapter implements
    CreateAnswerPort,
    UpdateAnswerOptionPort,
    LoadAnswerPort,
    LoadAnswersByQuestionnaireIdPort,
    GetQuestionnairesProgressPort,
    CountAnswersByQuestionIdsPort {

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
    public Optional<LoadAnswerPort.Result> loadAnswerIdAndOptionId(UUID assessmentResultId, Long questionId) {
        return repository.findByAssessmentResultIdAndQuestionId(assessmentResultId, questionId)
            .map(x -> new LoadAnswerPort.Result(x.getId(), x.getAnswerOptionId()));
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
    public int countByQuestionIds(UUID assessmentResultId, List<Long> questionIds) {
        return repository.getCountByQuestionIds(assessmentResultId, questionIds);
    }

    @Override
    public List<QuestionnaireProgress> getQuestionnairesProgressByAssessmentId(UUID assessmentId) {
        var assessmentResult = assessmentResultRepo.findFirstByAssessment_IdOrderByLastModificationTimeDesc(assessmentId)
            .orElseThrow(() -> new ResourceNotFoundException(GET_QUESTIONNAIRES_PROGRESS_ASSESSMENT_RESULT_NOT_FOUND));

        var progresses = repository.getQuestionnairesProgressByAssessmentResultId(assessmentResult.getId());
        return progresses.stream().map(p -> new QuestionnaireProgress(p.getQuestionnaireId(), p.getAnswerCount())).toList();
    }
}
