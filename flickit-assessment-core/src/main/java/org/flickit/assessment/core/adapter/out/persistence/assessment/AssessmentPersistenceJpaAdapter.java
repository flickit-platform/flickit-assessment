package org.flickit.assessment.core.adapter.out.persistence.assessment;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.SpaceAccessChecker;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.Assessment;
import org.flickit.assessment.core.application.domain.AssessmentColor;
import org.flickit.assessment.core.application.domain.AssessmentListItem;
import org.flickit.assessment.core.application.port.out.assessment.*;
import org.flickit.assessment.data.jpa.core.answer.AnswerJpaRepository;
import org.flickit.assessment.data.jpa.core.assessment.AssessmentJpaEntity;
import org.flickit.assessment.data.jpa.core.assessment.AssessmentJpaRepository;
import org.flickit.assessment.data.jpa.core.assessment.AssessmentKitSpaceJoinView;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaEntity;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaRepository;
import org.flickit.assessment.data.jpa.kit.assessmentkit.AssessmentKitJpaEntity;
import org.flickit.assessment.data.jpa.kit.assessmentkit.AssessmentKitJpaRepository;
import org.flickit.assessment.data.jpa.kit.maturitylevel.MaturityLevelJpaEntity;
import org.flickit.assessment.data.jpa.kit.maturitylevel.MaturityLevelJpaRepository;
import org.flickit.assessment.data.jpa.kit.question.QuestionJpaRepository;
import org.flickit.assessment.data.jpa.users.space.SpaceJpaEntity;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.flickit.assessment.core.application.domain.AssessmentUserRole.MANAGER;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;
import static org.flickit.assessment.data.jpa.core.assessment.AssessmentJpaEntity.Fields.ASSESSMENT_KIT_ID;
import static org.flickit.assessment.data.jpa.core.assessment.AssessmentJpaEntity.Fields.SPACE_ID;
import static org.springframework.data.jpa.domain.Specification.where;

@Component
@RequiredArgsConstructor
public class AssessmentPersistenceJpaAdapter implements
    CreateAssessmentPort,
    LoadAssessmentListPort,
    UpdateAssessmentPort,
    GetAssessmentProgressPort,
    GetAssessmentPort,
    DeleteAssessmentPort,
    CountAssessmentsPort,
    CheckUserAssessmentAccessPort,
    SpaceAccessChecker {

    private final AssessmentJpaRepository repository;
    private final AssessmentResultJpaRepository resultRepository;
    private final AnswerJpaRepository answerRepository;
    private final QuestionJpaRepository questionRepository;
    private final AssessmentKitJpaRepository kitRepository;
    private final MaturityLevelJpaRepository maturityLevelRepository;

    @Override
    public UUID persist(CreateAssessmentPort.Param param) {
        AssessmentJpaEntity unsavedEntity = AssessmentMapper.mapCreateParamToJpaEntity(param);
        AssessmentJpaEntity entity = repository.save(unsavedEntity);
        return entity.getId();
    }

    @Override
    public PaginatedResponse<AssessmentListItem> loadUserAssessments(Long kitId, UUID userId, int page, int size) {
        var pageResult = repository.findByUserId(kitId, userId, MANAGER.getId(), PageRequest.of(page, size));

        List<Long> kitVersionIds = pageResult.getContent().stream()
            .map(e -> e.getAssessmentKit().getKitVersionId())
            .toList();
        List<MaturityLevelJpaEntity> kitMaturityLevelEntities = maturityLevelRepository.findAllByKitVersionIdIn(kitVersionIds);
        Map<Long, List<MaturityLevelJpaEntity>> kitVersionIdToMaturityLevelEntities = kitMaturityLevelEntities.stream()
            .collect(Collectors.groupingBy(MaturityLevelJpaEntity::getKitVersionId));

        List<MaturityLevelJpaEntity.EntityId> assessmentMaturityLevelIds = pageResult.getContent().stream()
            .map(e -> new MaturityLevelJpaEntity.EntityId(e.getAssessmentResult().getMaturityLevelId(), e.getAssessmentKit().getKitVersionId()))
            .toList();

        Map<Long, MaturityLevelJpaEntity> maturityLevelIdToMaturityLevel =
            maturityLevelRepository.findAllById(assessmentMaturityLevelIds).stream()
                .collect(Collectors.toMap(MaturityLevelJpaEntity::getId, Function.identity()));

        List<AssessmentListItem> items = pageResult.getContent().stream()
            .map(e -> {
                AssessmentKitJpaEntity kitEntity = e.getAssessmentKit();
                List<MaturityLevelJpaEntity> kitLevelEntities = kitVersionIdToMaturityLevelEntities.get(kitEntity.getKitVersionId());
                AssessmentListItem.Kit kit = new AssessmentListItem.Kit(kitEntity.getId(), kitEntity.getTitle(), kitLevelEntities.size());
                SpaceJpaEntity spaceEntity = e.getSpace();
                AssessmentListItem.Space space = new AssessmentListItem.Space(spaceEntity.getId(), spaceEntity.getTitle());
                AssessmentListItem.MaturityLevel maturityLevel = null;
                if (Boolean.TRUE.equals(e.getAssessmentResult().getIsCalculateValid())) {
                    MaturityLevelJpaEntity maturityLevelEntity = maturityLevelIdToMaturityLevel.get(e.getAssessmentResult().getMaturityLevelId());
                    maturityLevel = new AssessmentListItem.MaturityLevel(maturityLevelEntity.getId(),
                        maturityLevelEntity.getTitle(),
                        maturityLevelEntity.getValue(),
                        maturityLevelEntity.getIndex());
                }

                return new AssessmentListItem(e.getAssessment().getId(),
                    e.getAssessment().getTitle(),
                    kit,
                    space,
                    AssessmentColor.valueOfById(e.getAssessment().getColorId()),
                    e.getAssessment().getLastModificationTime(),
                    maturityLevel,
                    e.getAssessmentResult().getIsCalculateValid(),
                    e.getAssessmentResult().getIsConfidenceValid(),
                    e.getManageable());
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
    public PaginatedResponse<AssessmentListItem> loadSpaceAssessments(Long spaceId,
                                                                      UUID userId,
                                                                      int page,
                                                                      int size) {
        var pageResult = repository.findBySpaceId(spaceId, MANAGER.getId(), userId,
            PageRequest.of(page, size, Sort.Direction.DESC, AssessmentResultJpaEntity.Fields.LAST_MODIFICATION_TIME));

        List<Long> kitVersionIds = pageResult.getContent().stream()
            .map(e -> e.getAssessmentResult().getKitVersionId())
            .toList();

        List<AssessmentKitJpaEntity> kitEntities = kitRepository.findAllByKitVersionIdIn(kitVersionIds);
        Map<Long, AssessmentKitJpaEntity> kitIdToKitEntity = kitEntities.stream()
            .collect(Collectors.toMap(AssessmentKitJpaEntity::getId, Function.identity()));

        List<MaturityLevelJpaEntity> kitMaturityLevelEntities = maturityLevelRepository.findAllByKitVersionIdIn(kitVersionIds);
        Map<Long, List<MaturityLevelJpaEntity>> kitVersionIdToMaturityLevelEntities = kitMaturityLevelEntities.stream()
            .collect(Collectors.groupingBy(MaturityLevelJpaEntity::getKitVersionId));

        Map<Long, MaturityLevelJpaEntity> maturityLevelIdToMaturityLevel =
            kitMaturityLevelEntities.stream()
                .collect(Collectors.toMap(MaturityLevelJpaEntity::getId, Function.identity()));

        List<AssessmentListItem> items = pageResult.getContent().stream()
            .map(e -> {
                AssessmentKitJpaEntity kitEntity = kitIdToKitEntity.get(e.getAssessment().getAssessmentKitId());
                List<MaturityLevelJpaEntity> kitLevelEntities = kitVersionIdToMaturityLevelEntities.get(kitEntity.getKitVersionId());
                AssessmentListItem.Kit kit = new AssessmentListItem.Kit(kitEntity.getId(), kitEntity.getTitle(), kitLevelEntities.size());
                AssessmentListItem.Space space = null;
                AssessmentListItem.MaturityLevel maturityLevel = null;
                if (Boolean.TRUE.equals(e.getAssessmentResult().getIsCalculateValid())) {
                    MaturityLevelJpaEntity maturityLevelEntity = maturityLevelIdToMaturityLevel.get(e.getAssessmentResult().getMaturityLevelId());
                    maturityLevel = new AssessmentListItem.MaturityLevel(maturityLevelEntity.getId(),
                        maturityLevelEntity.getTitle(),
                        maturityLevelEntity.getValue(),
                        maturityLevelEntity.getIndex());
                }

                return new AssessmentListItem(e.getAssessment().getId(),
                    e.getAssessment().getTitle(),
                    kit,
                    space,
                    AssessmentColor.valueOfById(e.getAssessment().getColorId()),
                    e.getAssessment().getLastModificationTime(),
                    maturityLevel,
                    e.getAssessmentResult().getIsCalculateValid(),
                    e.getAssessmentResult().getIsConfidenceValid(),
                    e.getManageable());
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
        if (!repository.existsByIdAndDeletedFalse(param.id()))
            throw new ResourceNotFoundException(UPDATE_ASSESSMENT_ID_NOT_FOUND);

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
    public GetAssessmentProgressPort.Result getProgress(UUID assessmentId) {
        var assessmentResult = resultRepository.findFirstByAssessment_IdOrderByLastModificationTimeDesc(assessmentId)
            .orElseThrow(() -> new ResourceNotFoundException(GET_ASSESSMENT_PROGRESS_ASSESSMENT_NOT_FOUND));

        int answersCount = answerRepository.getCountByAssessmentResultId(assessmentResult.getId());
        int questionsCount = questionRepository.countByKitVersionId(assessmentResult.getKitVersionId());
        return new GetAssessmentProgressPort.Result(assessmentId, answersCount, questionsCount);
    }

    @Override
    public Optional<Assessment> getAssessmentById(UUID assessmentId) {
        Optional<AssessmentKitSpaceJoinView> entity = repository.findByIdAndDeletedFalse(assessmentId);
        return entity.map(AssessmentMapper::mapToDomainModel);
    }

    @Override
    public void deleteById(UUID id, Long deletionTime) {
        if (!repository.existsByIdAndDeletedFalse(id))
            throw new ResourceNotFoundException(DELETE_ASSESSMENT_ID_NOT_FOUND);

        repository.delete(id, deletionTime);
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
