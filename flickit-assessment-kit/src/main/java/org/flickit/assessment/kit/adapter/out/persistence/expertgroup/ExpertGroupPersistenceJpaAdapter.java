package org.flickit.assessment.kit.adapter.out.persistence.expertgroup;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.data.jpa.kit.expertgroup.ExpertGroupJpaEntity;
import org.flickit.assessment.data.jpa.kit.expertgroup.ExpertGroupJpaRepository;
import org.flickit.assessment.kit.application.port.in.expertgroup.GetExpertGroupListUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupListPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
    public PaginatedResponse<GetExpertGroupListUseCase.ExpertGroupListItem> loadExpertGroupList(LoadExpertGroupListPort.Param param) {

        var pageResult = repository.getExpertGroupSummaries(PageRequest.of(param.page(), param.size()),param.currentUserID());
        List<GetExpertGroupListUseCase.ExpertGroupListItem> items = pageResult.getContent().stream()
            .map(ExpertGroupMapper::mapToExpertGroupListItem)
            .toList();


        List<GetExpertGroupListUseCase.ExpertGroupListItem> itemsWithMembers = new ArrayList<>();
        for (var item: items) {
            var members = repository.getMembersByExpert(item.id()).stream()
                .map(ExpertGroupMapper::mapToMember)
                .toList();
            itemsWithMembers.add(new GetExpertGroupListUseCase.ExpertGroupListItem(
                item.id(),
                item.title(),
                item.bio(),
                item.picture(),
                item.publishedKitsCount(),
                item.membersCount(),
                members,
                item.ownerId(),
                item.editable())
            );
        }

        return new PaginatedResponse<>(
            itemsWithMembers,
            pageResult.getNumber(),
            pageResult.getSize(),
            ExpertGroupJpaEntity.Fields.NAME,
            Sort.Direction.ASC.name().toLowerCase(),
            (int) pageResult.getTotalElements()
        );
    }
}
