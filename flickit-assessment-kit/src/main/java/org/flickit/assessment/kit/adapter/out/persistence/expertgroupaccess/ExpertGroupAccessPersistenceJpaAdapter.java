package org.flickit.assessment.kit.adapter.out.persistence.expertgroupaccess;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.data.jpa.kit.expertgroupaccess.ExpertGroupAccessJpaEntity;
import org.flickit.assessment.data.jpa.kit.expertgroupaccess.ExpertGroupAccessJpaRepository;
import org.flickit.assessment.data.jpa.kit.user.UserJpaEntity;
import org.flickit.assessment.kit.adapter.out.persistence.expertgroup.MembersMapper;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ExpertGroupAccessPersistenceJpaAdapter implements
    CreateExpertGroupAccessPort,
    CheckExpertGroupAccessPort,
    LoadExpertGroupMembersPort {

    private final ExpertGroupAccessJpaRepository repository;

    @Override
    public boolean checkIsMember(long expertGroupId, UUID userId) {
        return repository.existsByExpertGroupIdAndUserId(expertGroupId, userId);
    }

    @Override
    public PaginatedResponse<LoadExpertGroupMembersPort.Result> loadExpertGroupMembers(LoadExpertGroupMembersPort.Param param) {
        var pageResult = repository.findExpertGroupMembers(param.expertGroupId(),
            PageRequest.of(param.page(), param.size(), Sort.Direction.ASC, UserJpaEntity.Fields.NAME));

        var items = pageResult
            .stream()
            .map(MembersMapper::mapToResult)
            .toList();

        return new PaginatedResponse<>(
            items,
            pageResult.getNumber(),
            pageResult.getSize(),
            UserJpaEntity.Fields.NAME,
            Sort.Direction.ASC.name().toLowerCase(),
            (int) pageResult.getTotalElements()
        );
    }

    @Override
    public Long persist(CreateExpertGroupAccessPort.Param param) {
        ExpertGroupAccessJpaEntity unsavedEntity = ExpertGroupAccessMapper.mapCreateParamToJpaEntity(param);
        ExpertGroupAccessJpaEntity savedEntity = repository.save(unsavedEntity);
        return savedEntity.getId();
    }
}
