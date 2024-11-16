package org.flickit.assessment.users.adapter.out.persistence.expertgroupaccess;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.data.jpa.users.expertgroup.ExpertGroupJpaRepository;
import org.flickit.assessment.data.jpa.users.expertgroup.ExpertGroupMembersView;
import org.flickit.assessment.data.jpa.users.expertgroupaccess.ExpertGroupAccessJpaEntity;
import org.flickit.assessment.data.jpa.users.expertgroupaccess.ExpertGroupAccessJpaRepository;
import org.flickit.assessment.users.application.domain.ExpertGroupAccess;
import org.flickit.assessment.users.application.port.out.expertgroupaccess.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.flickit.assessment.users.common.ErrorMessageKey.DELETE_EXPERT_GROUP_MEMBER_USER_ID_NOT_FOUND;
import static org.flickit.assessment.users.common.ErrorMessageKey.EXPERT_GROUP_ID_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class ExpertGroupAccessPersistenceJpaAdapter implements
    CreateExpertGroupAccessPort,
    LoadExpertGroupMembersPort,
    InviteExpertGroupMemberPort,
    LoadExpertGroupMemberStatusPort,
    LoadExpertGroupAccessPort,
    ConfirmExpertGroupInvitationPort,
    DeleteExpertGroupMemberPort,
    UpdateExpertGroupLastSeenPort {

    private final ExpertGroupAccessJpaRepository repository;
    private final ExpertGroupJpaRepository expertGroupRepository;

    @Override
    public PaginatedResponse<Member> loadExpertGroupMembers(long expertGroupId, int status, int page, int size) {
        if (!expertGroupJpaRepository.existsById(expertGroupId))
        if (!expertGroupRepository.existsById(expertGroupId))
            throw new ResourceNotFoundException(EXPERT_GROUP_ID_NOT_FOUND);

        var pageResult = repository.findExpertGroupMembers(expertGroupId, status.ordinal(), LocalDateTime.now(),
            PageRequest.of(page, size, Sort.Direction.DESC, ExpertGroupAccessJpaEntity.Fields.LAST_MODIFICATION_TIME));

        var items = pageResult
            .stream()
            .map(ExpertGroupAccessPersistenceJpaAdapter::mapToResult)
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

    @Override
    public Optional<ExpertGroupAccess> loadExpertGroupAccess(long expertGroupId, UUID userId) {
        return repository.findByExpertGroupIdAndAndUserId(expertGroupId, userId)
            .map(ExpertGroupAccessMapper::mapAccessJpaToExpertGroupAccessModel);
    }

    @Override
    public void confirmInvitation(long expertGroupId, UUID userId) {
        repository.confirmInvitation(expertGroupId, userId, LocalDateTime.now());
    }

    @Override
    public void deleteMember(long expertGroupId, UUID userId) {
        ExpertGroupAccessJpaEntity entity = repository.findByExpertGroupIdAndAndUserId(expertGroupId, userId)
            .orElseThrow(() -> new ResourceNotFoundException(DELETE_EXPERT_GROUP_MEMBER_USER_ID_NOT_FOUND));
        repository.delete(entity);
    }

    @Override
    public void updateLastSeen(long expertGroupId, UUID userId, LocalDateTime currentTime) {
        repository.updateLastSeen(expertGroupId, userId, currentTime);
    }
}
