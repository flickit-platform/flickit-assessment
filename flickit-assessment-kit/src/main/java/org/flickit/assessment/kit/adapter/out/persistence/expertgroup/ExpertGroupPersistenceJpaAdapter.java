package org.flickit.assessment.kit.adapter.out.persistence.expertgroup;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.data.jpa.kit.expertgroup.ExpertGroupJpaEntity;
import org.flickit.assessment.data.jpa.kit.expertgroup.ExpertGroupJpaRepository;
import org.flickit.assessment.data.jpa.kit.expertgroup.ExpertGroupWithDetailsView;
import org.flickit.assessment.data.jpa.kit.user.UserJpaEntity;
import org.flickit.assessment.kit.application.port.in.expertgroup.GetExpertGroupListUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupListPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupMemberIdsPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupPort;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.flickit.assessment.kit.adapter.out.persistence.expertgroup.ExpertGroupMapper.mapEntityToPortResult;
import static org.flickit.assessment.kit.adapter.out.persistence.expertgroup.ExpertGroupMapper.mapToPortResult;
import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_EXPERT_GROUP_EXPERT_GROUP_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class ExpertGroupPersistenceJpaAdapter implements
    LoadExpertGroupOwnerPort,
    LoadExpertGroupListPort,
    LoadExpertGroupMemberIdsPort,
    LoadExpertGroupPort {

    private final ExpertGroupJpaRepository repository;

    @Override
    public Optional<UUID> loadOwnerId(Long expertGroupId) {
        return Optional.of(repository.loadOwnerIdById(expertGroupId));
    }

    @Override
    public PaginatedResponse<LoadExpertGroupListPort.Result> loadExpertGroupList(LoadExpertGroupListPort.Param param) {
        var pageResult = repository.findByUserId(
            param.currentUserId(),
            PageRequest.of(param.page(), param.size()));

        List<LoadExpertGroupListPort.Result> items = pageResult.getContent().stream()
            .map(e -> resultWithMembers(e, param.sizeOfMembers()))
            .toList();

        return new PaginatedResponse<>(
            items,
            pageResult.getNumber(),
            pageResult.getSize(),
            ExpertGroupJpaEntity.Fields.NAME,
            Sort.Direction.ASC.name().toLowerCase(),
            (int) pageResult.getTotalElements()
        );
    }

    private LoadExpertGroupListPort.Result resultWithMembers(ExpertGroupWithDetailsView item, int membersCount) {
        var members = repository.findMembersByExpertGroupId(item.getId(),
                PageRequest.of(0, membersCount, Sort.Direction.ASC, UserJpaEntity.Fields.NAME))
            .stream()
            .map(GetExpertGroupListUseCase.Member::new)
            .toList();
        return mapToPortResult(item, members);
    }

    @Override
    public List<LoadExpertGroupMemberIdsPort.Result> loadMemberIds(long expertGroupId) {
        List<UUID> memberIds = repository.findMemberIdsByExpertGroupId(expertGroupId);
        return memberIds.stream()
            .map(LoadExpertGroupMemberIdsPort.Result::new)
            .toList();
    }

    @Override
    public LoadExpertGroupPort.Result loadExpertGroup(LoadExpertGroupPort.Param param) {
        var resultEntity = repository.findById(param.id()).orElseThrow(
            () -> new ResourceNotFoundException(GET_EXPERT_GROUP_EXPERT_GROUP_NOT_FOUND));
        return mapEntityToPortResult(resultEntity);
    }
}
