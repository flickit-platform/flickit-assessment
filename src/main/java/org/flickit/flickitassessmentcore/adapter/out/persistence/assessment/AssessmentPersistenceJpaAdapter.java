package org.flickit.flickitassessmentcore.adapter.out.persistence.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.adapter.out.persistence.answer.AnswerJpaRepository;
import org.flickit.flickitassessmentcore.adapter.out.persistence.assessmentresult.AssessmentResultJpaRepository;
import org.flickit.flickitassessmentcore.application.domain.crud.PaginatedResponse;
import org.flickit.flickitassessmentcore.application.port.in.assessment.GetAssessmentListUseCase.AssessmentListItem;
import org.flickit.flickitassessmentcore.application.port.out.assessment.CreateAssessmentPort;
import org.flickit.flickitassessmentcore.application.port.out.assessment.GetAssessmentProgressPort;
import org.flickit.flickitassessmentcore.application.port.out.assessment.LoadAssessmentListItemsBySpacePort;
import org.flickit.flickitassessmentcore.application.port.out.assessment.UpdateAssessmentPort;
import org.flickit.flickitassessmentcore.application.service.exception.ResourceNotFoundException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.GET_ASSESSMENT_PROGRESS_ASSESSMENT_RESULT_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class AssessmentPersistenceJpaAdapter implements
    CreateAssessmentPort,
    LoadAssessmentListItemsBySpacePort,
    UpdateAssessmentPort,
    GetAssessmentProgressPort {

    private final AssessmentJpaRepository repository;
    private final AssessmentResultJpaRepository resultRepository;
    private final AnswerJpaRepository answerRepository;

    @Override
    public UUID persist(CreateAssessmentPort.Param param) {
        AssessmentJpaEntity unsavedEntity = AssessmentMapper.mapCreateParamToJpaEntity(param);
        AssessmentJpaEntity entity = repository.save(unsavedEntity);
        return entity.getId();
    }

    @Override
    public PaginatedResponse<AssessmentListItem> loadAssessments(Long spaceId, int page, int size) {
        var pageResult = repository.findBySpaceIdOrderByLastModificationTimeDesc(spaceId, PageRequest.of(page, size));
        var items = pageResult.getContent().stream()
            .map(AssessmentMapper::mapToAssessmentListItem)
            .toList();
        return new PaginatedResponse<>(
            items,
            pageResult.getNumber(),
            pageResult.getSize(),
            AssessmentJpaEntity.Fields.LAST_MODIFICATION_TIME,
            Sort.Direction.DESC.name().toLowerCase(),
            (int) pageResult.getTotalElements()
        );
    }

    @Override
    public UpdateAssessmentPort.Result update(UpdateAssessmentPort.Param param) {
        repository.update(
            param.id(),
            param.title(),
            param.code(),
            param.colorId(),
            param.lastModificationTime());
        return new UpdateAssessmentPort.Result(param.id());
    }

    @Override
    public GetAssessmentProgressPort.Result getAssessmentProgressById(UUID assessmentId) {
        var assessmentResult = resultRepository.findFirstByAssessment_IdOrderByLastModificationTimeDesc(assessmentId)
            .orElseThrow(() -> new ResourceNotFoundException(GET_ASSESSMENT_PROGRESS_ASSESSMENT_RESULT_NOT_FOUND));

        int answersCount = answerRepository.getCountByAssessmentResultId(assessmentResult.getId());
        return new GetAssessmentProgressPort.Result(assessmentId, answersCount);
    }
}
