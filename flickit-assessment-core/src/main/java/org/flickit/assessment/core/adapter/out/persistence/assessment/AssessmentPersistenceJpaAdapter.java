package org.flickit.assessment.core.adapter.out.persistence.assessment;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.Assessment;
import org.flickit.assessment.core.application.domain.AssessmentColor;
import org.flickit.assessment.core.application.port.in.assessment.GetAssessmentListUseCase.AssessmentListItem;
import org.flickit.assessment.core.application.port.out.assessment.*;
import org.flickit.assessment.data.jpa.core.answer.AnswerJpaRepository;
import org.flickit.assessment.data.jpa.core.assessment.AssessmentJpaEntity;
import org.flickit.assessment.data.jpa.core.assessment.AssessmentJpaRepository;
import org.flickit.assessment.data.jpa.core.assessment.AssessmentListItemView;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaRepository;
import org.flickit.assessment.data.jpa.kit.assessmentkit.AssessmentKitJpaEntity;
import org.flickit.assessment.data.jpa.kit.assessmentkit.AssessmentKitJpaRepository;
import org.flickit.assessment.data.jpa.kit.maturitylevel.MaturityLevelJpaEntity;
import org.flickit.assessment.data.jpa.kit.maturitylevel.MaturityLevelJpaRepository;
import org.flickit.assessment.data.jpa.users.space.SpaceJpaEntity;
import org.flickit.assessment.data.jpa.users.space.SpaceJpaRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.flickit.assessment.core.common.ErrorMessageKey.GET_ASSESSMENT_PROGRESS_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.data.jpa.core.assessment.AssessmentJpaEntity.Fields.ASSESSMENT_KIT_ID;
import static org.flickit.assessment.data.jpa.core.assessment.AssessmentJpaEntity.Fields.SPACE_ID;
import static org.springframework.data.jpa.domain.Specification.where;

@Component
@RequiredArgsConstructor
public class AssessmentPersistenceJpaAdapter implements
    CreateAssessmentPort,
    LoadAssessmentListItemsBySpacePort,
    UpdateAssessmentPort,
    GetAssessmentProgressPort,
    GetAssessmentPort,
    DeleteAssessmentPort,
    CheckAssessmentExistencePort,
    CountAssessmentsPort,
    CheckUserAssessmentAccessPort {

    private final AssessmentJpaRepository repository;
    private final AssessmentResultJpaRepository resultRepository;
    private final AnswerJpaRepository answerRepository;
    private final AssessmentKitJpaRepository kitRepository;
    private final SpaceJpaRepository spaceRepository;
    private final MaturityLevelJpaRepository maturityLevelRepository;

    @Override
    public UUID persist(CreateAssessmentPort.Param param) {
        AssessmentJpaEntity unsavedEntity = AssessmentMapper.mapCreateParamToJpaEntity(param);
        AssessmentJpaEntity entity = repository.save(unsavedEntity);
        return entity.getId();
    }

    @Override
    public PaginatedResponse<AssessmentListItem> loadNotDeletedAssessments(List<Long> spaceIds, Long kitId, int page, int size) {
        var pageResult = repository.findBySpaceIdAndDeletedFalseOrderByLastModificationTimeDesc(spaceIds, kitId, PageRequest.of(page, size));

        Map<UUID, Long> assessmentIdToMaturityLevelId = pageResult.getContent().stream()
            .collect(Collectors.toMap(e -> e.getAssessment().getId(), AssessmentListItemView::getMaturityLevelId));

        List<AssessmentJpaEntity> assessmentEntities = pageResult.getContent().stream()
            .map(AssessmentListItemView::getAssessment)
            .toList();

        Set<Long> kitIds = assessmentEntities.stream()
            .map(AssessmentJpaEntity::getAssessmentKitId)
            .collect(Collectors.toSet());
        List<AssessmentKitJpaEntity> kitEntities = kitRepository.findAllById(kitIds);
        Map<Long, AssessmentKitJpaEntity> kitIdToKitEntity = kitEntities.stream()
            .collect(Collectors.toMap(AssessmentKitJpaEntity::getId, Function.identity()));

        Set<Long> assessmentSpaceIds = assessmentEntities.stream()
            .map(AssessmentJpaEntity::getSpaceId)
            .collect(Collectors.toSet());
        List<SpaceJpaEntity> spaceEntities = spaceRepository.findAllById(assessmentSpaceIds);
        Map<Long, SpaceJpaEntity> spaceIdToSpaceEntity = spaceEntities.stream()
            .collect(Collectors.toMap(SpaceJpaEntity::getId, Function.identity()));

        List<Long> kitVersionIds = kitEntities.stream()
            .map(AssessmentKitJpaEntity::getKitVersionId)
            .toList();
        List<MaturityLevelJpaEntity> kitMaturityLevelEntities = maturityLevelRepository.findAllByKitVersionIdIn(kitVersionIds);
        Map<Long, List<MaturityLevelJpaEntity>> kitVersionIdToMaturityLevelEntities = kitMaturityLevelEntities.stream()
            .collect(Collectors.groupingBy(MaturityLevelJpaEntity::getKitVersionId));

        List<Long> assessmentMaturityLevelIds = pageResult.getContent().stream()
            .map(AssessmentListItemView::getMaturityLevelId)
            .toList();

        Map<Long, MaturityLevelJpaEntity> maturityLevelIdToMaturityLevel =
            maturityLevelRepository.findAllById(assessmentMaturityLevelIds).stream()
            .collect(Collectors.toMap(MaturityLevelJpaEntity::getId, Function.identity()));

        List<AssessmentListItem> items = pageResult.getContent().stream()
            .map(e -> {
                AssessmentKitJpaEntity kitEntity = kitIdToKitEntity.get(e.getAssessment().getAssessmentKitId());
                List<MaturityLevelJpaEntity> kitLevelEntities = kitVersionIdToMaturityLevelEntities.get(kitEntity.getKitVersionId());
                AssessmentListItem.Kit kit = new AssessmentListItem.Kit(kitEntity.getId(), kitEntity.getTitle(), kitLevelEntities.size());
                SpaceJpaEntity spaceEntity = spaceIdToSpaceEntity.get(e.getAssessment().getSpaceId());
                AssessmentListItem.Space space = new AssessmentListItem.Space(spaceEntity.getId(), spaceEntity.getTitle());
                Long maturityLevelId = assessmentIdToMaturityLevelId.get(e.getAssessment().getId());
                MaturityLevelJpaEntity maturityLevelEntity = maturityLevelIdToMaturityLevel.get(maturityLevelId);
                AssessmentListItem.MaturityLevel maturityLevel = new AssessmentListItem.MaturityLevel(maturityLevelEntity.getId(),
                    maturityLevelEntity.getTitle(),
                    maturityLevelEntity.getValue(),
                    maturityLevelEntity.getIndex());

                return new AssessmentListItem(e.getAssessment().getId(),
                    e.getAssessment().getTitle(),
                    kit,
                    space,
                    AssessmentColor.valueOfById(e.getAssessment().getColorId()),
                    e.getAssessment().getLastModificationTime(),
                    maturityLevel,
                    e.getIsCalculateValid(),
                    e.getIsConfidenceValid());
            }).toList();


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
    public UpdateAssessmentPort.Result update(UpdateAssessmentPort.AllParam param) {
        repository.update(
            param.id(),
            param.title(),
            param.code(),
            param.colorId(),
            param.lastModificationTime(),
            param.lastModifiedBy());
        return new UpdateAssessmentPort.Result(param.id());
    }

    @Override
    public GetAssessmentProgressPort.Result getAssessmentProgressById(UUID assessmentId) {
        var assessmentResult = resultRepository.findFirstByAssessment_IdOrderByLastModificationTimeDesc(assessmentId)
            .orElseThrow(() -> new ResourceNotFoundException(GET_ASSESSMENT_PROGRESS_ASSESSMENT_RESULT_NOT_FOUND));

        int answersCount = answerRepository.getCountByAssessmentResultId(assessmentResult.getId());
        return new GetAssessmentProgressPort.Result(assessmentId, answersCount);
    }

    @Override
    public Optional<Assessment> getAssessmentById(UUID assessmentId) {
        Optional<AssessmentJpaEntity> entity = repository.findById(assessmentId);
        return entity.map(AssessmentMapper::mapToDomainModel);
    }

    @Override
    public void deleteById(UUID id, Long deletionTime) {
        repository.delete(id, deletionTime);
    }

    @Override
    public boolean existsById(UUID id) {
        return repository.existsByIdAndDeletedFalse(id);
    }

    @Override
    public CountAssessmentsPort.Result count(CountAssessmentsPort.Param param) {
        Integer totalCount = null;
        Integer deletedCount = null;
        Integer notDeletedCount = null;

        Specification<AssessmentJpaEntity> baseCondition = (r, cq, cb) -> {
            Predicate p = cb.and();
            if (param.kitId() != null)
                p = cb.and(cb.equal(r.get(ASSESSMENT_KIT_ID), param.kitId()), p);
            if (param.spaceId() != null)
                p = cb.and(cb.equal(r.get(SPACE_ID), param.spaceId()), p);
            return p;
        };

        if (param.deleted()) {
            deletedCount = (int) repository.count(where(baseCondition).and(deletedCondition(true)));
        }
        if (param.notDeleted()) {
            notDeletedCount = (int) repository.count(where(baseCondition).and(deletedCondition(false)));
        }
        if (param.total()) {
            if (param.deleted() && param.notDeleted())
                totalCount = deletedCount + notDeletedCount;
            else
                totalCount = (int) repository.count(where(baseCondition));
        }

        return new CountAssessmentsPort.Result(totalCount, deletedCount, notDeletedCount);
    }

    private static Specification<AssessmentJpaEntity> deletedCondition(boolean deleted) {
        return (r, cq, cb) -> cb.equal(r.get("deleted"), deleted);
    }

    @Override
    public void updateLastModificationTime(UUID id, LocalDateTime lastModificationTime) {
        repository.updateLastModificationTime(id, lastModificationTime);
    }

    @Override
    public boolean hasAccess(UUID assessmentId, UUID userId) {
        return repository.checkUserAccess(assessmentId, userId).isPresent();
    }
}
