package org.flickit.assessment.users.adapter.out.persistence.expertgroupaccess;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.data.jpa.users.expertgroup.ExpertGroupMembersView;
import org.flickit.assessment.data.jpa.users.expertgroupaccess.ExpertGroupAccessJpaEntity;
import org.flickit.assessment.data.jpa.users.expertgroupaccess.ExpertGroupAccessJpaRepository;
import org.flickit.assessment.users.application.port.out.expertgroupaccess.CreateExpertGroupAccessPort;
import org.flickit.assessment.users.application.port.out.expertgroupaccess.InviteExpertGroupMemberPort;
import org.flickit.assessment.users.application.port.out.expertgroupaccess.LoadExpertGroupMemberStatusPort;
import org.flickit.assessment.users.application.port.out.expertgroupaccess.LoadExpertGroupMembersPort;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ExpertGroupAccessPersistenceJpaAdapter implements
    CreateExpertGroupAccessPort,
    LoadExpertGroupMembersPort,
    InviteExpertGroupMemberPort,
    LoadExpertGroupMemberStatusPort {

    private final ExpertGroupAccessJpaRepository repository;

    @Override
    public PaginatedResponse<Member> loadExpertGroupMembers(long expertGroupId, int status, int page, int size) {
        var pageResult = repository.findExpertGroupMembers(expertGroupId, status,
            PageRequest.of(page, size, Sort.Direction.DESC, ExpertGroupAccessJpaEntity.Fields.CREATION_TIME));

        var items = pageResult
            .stream()
            .map(ExpertGroupAccessPersistenceJpaAdapter::mapToResult)
            .toList();

        return new PaginatedResponse<>(
            items,
            pageResult.getNumber(),
            pageResult.getSize(),
            ExpertGroupAccessJpaEntity.Fields.CREATION_TIME,
            Sort.Direction.DESC.name().toLowerCase(),
            (int) pageResult.getTotalElements()
        );
    }

    @Override
    public void persist(CreateExpertGroupAccessPort.Param param) {
        ExpertGroupAccessJpaEntity unsavedEntity = ExpertGroupAccessMapper.mapCreateParamToJpaEntity(param);
        repository.save(unsavedEntity);
    }

    @Override
    public void invite(InviteExpertGroupMemberPort.Param param) {
        ExpertGroupAccessJpaEntity unsavedEntity = (ExpertGroupAccessMapper.mapInviteParamToJpaEntity(param));
        repository.save(unsavedEntity);
    }

    private static LoadExpertGroupMembersPort.Member mapToResult(ExpertGroupMembersView view) {
        return new LoadExpertGroupMembersPort.Member(
            view.getId(),
            view.getEmail(),
            view.getDisplayName(),
            view.getBio(),
            view.getPicture(),
            view.getLinkedin(),
            view.getStatus(),
            view.getInviteExpirationDate());
    }

    @Override
    public Optional<Integer> getMemberStatus(long expertGroupId, UUID userId) {
        return repository.findExpertGroupMemberStatus(expertGroupId, userId);
    }
}
