package org.flickit.assessment.users.adapter.out.persistence.expertgroup;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.data.jpa.users.expertgroup.*;
import org.flickit.assessment.data.jpa.users.expertgroupaccess.ExpertGroupAccessJpaEntity;
import org.flickit.assessment.users.application.domain.ExpertGroup;
import org.flickit.assessment.users.application.domain.ExpertGroupMember;
import org.flickit.assessment.users.application.port.in.expertgroup.GetExpertGroupListUseCase;
import org.flickit.assessment.users.application.port.out.expertgroup.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.flickit.assessment.users.adapter.out.persistence.expertgroup.ExpertGroupMapper.*;
import static org.flickit.assessment.users.common.ErrorMessageKey.EXPERT_GROUP_ID_NOT_FOUND;
import static org.flickit.assessment.users.common.ErrorMessageKey.GET_EXPERT_GROUP_EXPERT_GROUP_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class ExpertGroupPersistenceJpaAdapter implements
    LoadExpertGroupOwnerPort,
    LoadExpertGroupListPort,
    CreateExpertGroupPort,
    LoadExpertGroupPort,
    CheckExpertGroupExistsPort,
    DeleteExpertGroupPort,
    CountExpertGroupKitsPort,
    UpdateExpertGroupPort,
    UpdateExpertGroupPicturePort {

    private final ExpertGroupJpaRepository repository;

    @Override
    public UUID loadOwnerId(Long expertGroupId) {
        return repository.loadOwnerIdById(expertGroupId)
            .orElseThrow(() -> new ResourceNotFoundException(EXPERT_GROUP_ID_NOT_FOUND));
    }

    @Override
    public Long persist(CreateExpertGroupPort.Param param) {
        ExpertGroupJpaEntity unsavedEntity = ExpertGroupMapper.mapCreateParamToJpaEntity(param);
        ExpertGroupJpaEntity savedEntity = repository.save(unsavedEntity);
        return savedEntity.getId();
    }

    @Override
    public PaginatedResponse<LoadExpertGroupListPort.Result> loadExpertGroupList(LoadExpertGroupListPort.Param param) {
        var pageResult = repository.findByUserId(
            param.currentUserId(),
            PageRequest.of(param.page(), param.size()));

        List<Long> expertGroupIdList = pageResult.getContent().stream()
            .map(ExpertGroupWithDetailsView::getId)
            .toList();

        var members = repository.findMembersByExpertGroupId(expertGroupIdList)
            .stream()
            .map(e -> new ExpertGroupMember(e.getExpertGroupId(), e.getId(), e.getDisplayName()))
            .toList();

        var expertGroupMembersCountMap = repository.expertGroupMembersCount(expertGroupIdList).stream()
            .collect(Collectors.toMap(ExpertGroupMembersCountView::getId, ExpertGroupMembersCountView::getMembersCount));

        List<LoadExpertGroupListPort.Result> items = pageResult.getContent().stream()
            .map(e -> resultWithMembers(e,  expertGroupMembersCountMap.get(e.getId()), members))
            .toList();

        return new PaginatedResponse<>(
            items,
            pageResult.getNumber(),
            pageResult.getSize(),
            ExpertGroupAccessJpaEntity.Fields.LAST_MODIFICATION_TIME,
            Sort.Direction.DESC.name().toLowerCase(),
            (int) pageResult.getTotalElements()
        );
    }

    private LoadExpertGroupListPort.Result resultWithMembers(ExpertGroupWithDetailsView item, int membersCount, List<ExpertGroupMember> members) {
        var relatedMembers = members.stream()
            .filter(m -> Objects.equals(m.getExpertGroupId(), item.getId()))
            .map(m -> new GetExpertGroupListUseCase.Member(m.getDisplayName()))
            .sorted(Comparator.comparing(GetExpertGroupListUseCase.Member::displayName))
            .toList();
        return mapToPortResult(item, relatedMembers, membersCount);
    }

    @Override
    public ExpertGroup loadExpertGroup(long id) {
        var resultEntity = repository.findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new ResourceNotFoundException(GET_EXPERT_GROUP_EXPERT_GROUP_NOT_FOUND));
        return mapToDomainModel(resultEntity);
    }

    @Override
    public boolean existsById(long id) {
        return repository.existsByIdAndDeletedFalse(id);
    }

    @Override
    public void deleteById(long expertGroupId, long deletionTime) {
        repository.delete(expertGroupId, deletionTime);
    }

    @Override
    public CountExpertGroupKitsPort.Result countKits(long expertGroupId) {
        var resultEntity = repository.countKits(expertGroupId);
        return mapKitsCountToPortResult(resultEntity);
    }

    @Override
    public void update(UpdateExpertGroupPort.Param param) {
        repository.update(param.id(), param.code(), param.title(), param.bio(), param.about(), param.website(),
            param.lastModificationTime(), param.lastModifiedBy());
    }

    @Override
    public void updatePicture(long expertGroupId, String picture) {
        repository.updatePicture(expertGroupId, picture);
    }
}
