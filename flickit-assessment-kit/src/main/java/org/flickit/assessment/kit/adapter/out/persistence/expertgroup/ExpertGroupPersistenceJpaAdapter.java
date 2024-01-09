package org.flickit.assessment.kit.adapter.out.persistence.expertgroup;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.function.TriFunction;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.data.jpa.kit.expertgroup.ExpertGroupJpaEntity;
import org.flickit.assessment.data.jpa.kit.expertgroup.ExpertGroupJpaRepository;
import org.flickit.assessment.kit.application.port.in.expertgroup.GetExpertGroupListUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupListPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.UnaryOperator;

@Component
@RequiredArgsConstructor
public class ExpertGroupPersistenceJpaAdapter implements
    LoadExpertGroupOwnerPort,
    LoadExpertGroupListPort {

    private final ExpertGroupJpaRepository repository;

    @Override
    public Optional<UUID> loadOwnerId(Long expertGroupId) {
        return Optional.of(repository.loadOwnerIdById(expertGroupId));
    }

    @Override
    public PaginatedResponse<Result> loadExpertGroupList(Param param) {

        UnaryOperator<Result> mapMembersWithRepository
            = item -> resultWithMembers.apply(repository,param, item);

        var pageResult = repository.findByUserId(
            param.currentUserId(),
            PageRequest.of(param.page(), param.size()));

        List<Result> items = pageResult.getContent().stream()
            .map(ExpertGroupMapper::mapToPortResult)
            .map(mapMembersWithRepository)
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

    static TriFunction<ExpertGroupJpaRepository,Param, Result, Result>
        resultWithMembers = (repository,param, item) -> {
        var members = repository.findMembersByExpertId(item.id(),PageRequest.of(0, param.sizeOfMembers()))
            .stream()
            .map(GetExpertGroupListUseCase.Member::new)
            .toList();

        return new Result(item.id(), item.title(), item.bio(), item.picture(),
            item.publishedKitsCount(), item.membersCount(), members, item.ownerId()
        );
    };
}
