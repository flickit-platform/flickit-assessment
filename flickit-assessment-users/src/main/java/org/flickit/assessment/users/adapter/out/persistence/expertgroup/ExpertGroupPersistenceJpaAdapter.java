package org.flickit.assessment.users.adapter.out.persistence.expertgroup;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.data.jpa.users.expertgroup.ExpertGroupJpaEntity;
import org.flickit.assessment.data.jpa.users.expertgroup.ExpertGroupJpaRepository;
import org.flickit.assessment.data.jpa.users.expertgroup.ExpertGroupWithDetailsView;
import org.flickit.assessment.data.jpa.users.user.UserJpaEntity;
import org.flickit.assessment.users.application.domain.ExpertGroup;
import org.flickit.assessment.users.application.port.in.expertgroup.GetExpertGroupListUseCase;
import org.flickit.assessment.users.application.port.out.expertgroup.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.flickit.assessment.users.adapter.out.persistence.expertgroup.ExpertGroupMapper.mapToDomainModel;
import static org.flickit.assessment.users.adapter.out.persistence.expertgroup.ExpertGroupMapper.mapToPortResult;
import static org.flickit.assessment.users.common.ErrorMessageKey.GET_EXPERT_GROUP_EXPERT_GROUP_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class ExpertGroupPersistenceJpaAdapter implements
    LoadExpertGroupListPort,
    CreateExpertGroupPort,
    LoadExpertGroupPort,
    CheckExpertGroupExistsPort,
    LoadExpertGroupOwnerPort {

    private final ExpertGroupJpaRepository repository;

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
    public ExpertGroup loadExpertGroup(long id) {
        var resultEntity = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(GET_EXPERT_GROUP_EXPERT_GROUP_NOT_FOUND));
        return mapToDomainModel(resultEntity);
    }

    @Override
    public boolean existsById(long id) {
        return repository.existsById(id);
    }

    @Override
    public Optional<UUID> loadOwnerId(Long expertGroupId) {
        return Optional.of(repository.loadOwnerIdById(expertGroupId));
    }
}
