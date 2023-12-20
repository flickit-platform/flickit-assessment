package org.flickit.assessment.kit.adapter.out.persistence.assessmentkit;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.data.jpa.kit.assessmentkit.AssessmentKitJpaEntity;
import org.flickit.assessment.data.jpa.kit.assessmentkit.AssessmentKitJpaRepository;
import org.flickit.assessment.data.jpa.kit.expertgroup.ExpertGroupJpaRepository;
import org.flickit.assessment.data.jpa.kit.user.UserJpaEntity;
import org.flickit.assessment.data.jpa.kit.user.UserJpaRepository;
import org.flickit.assessment.kit.adapter.out.persistence.user.UserMapper;
import org.flickit.assessment.kit.application.domain.crud.KitUserPaginatedResponse;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadExpertGroupIdPort;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadKitUsersPort;
import org.flickit.assessment.kit.application.port.out.useraccess.GrantUserAccessToKitPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static org.flickit.assessment.kit.common.ErrorMessageKey.*;
import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_KIT_USER_LIST_EXPERT_GROUP_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class AssessmentKitPersistenceJpaAdapter implements
    GrantUserAccessToKitPort,
    LoadExpertGroupIdPort,
    LoadKitUsersPort {

    private final AssessmentKitJpaRepository repository;
    private final ExpertGroupJpaRepository expertGroupRepository;
    private final UserJpaRepository userRepository;

    @Override
    public void grantUserAccess(Long kitId, String email) {
        AssessmentKitJpaEntity assessmentKit = repository.findById(kitId)
            .orElseThrow(() -> new ResourceNotFoundException(GRANT_USER_ACCESS_TO_KIT_KIT_ID_NOT_FOUND));
        UserJpaEntity user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException(GRANT_USER_ACCESS_TO_KIT_EMAIL_NOT_FOUND));

        assessmentKit.getAccessGrantedUsers().add(user);
        repository.save(assessmentKit);
    }

    @Override
    public Optional<Long> loadExpertGroupId(Long kitId) {
        return Optional.of(repository.loadExpertGroupIdById(kitId));
    }

    @Override
    public KitUserPaginatedResponse load(LoadKitUsersPort.Param param) {
        var kit = repository.findById(param.kitId()).orElseThrow(
            () -> new ResourceNotFoundException(GET_KIT_USER_LIST_KIT_NOT_FOUND));
        var expertGroup = expertGroupRepository.findById(kit.getExpertGroupId()).orElseThrow(
            () -> new ResourceNotFoundException(GET_KIT_USER_LIST_EXPERT_GROUP_NOT_FOUND));

        Page<UserJpaEntity> pageResult = repository.findAllKitUsers(
            param.kitId(),
            PageRequest.of(param.page(), param.size()));

        var items = pageResult.getContent().stream()
            .map(UserMapper::mapToUserListItem)
            .toList();

        PaginatedResponse<KitUserPaginatedResponse.UserListItem> result = new PaginatedResponse<>(
            items,
            pageResult.getNumber(),
            pageResult.getSize(),
            UserJpaEntity.Fields.NAME,
            Sort.Direction.ASC.name().toLowerCase(),
            (int) pageResult.getTotalElements()
        );

        return new KitUserPaginatedResponse(
            result,
            new KitUserPaginatedResponse.Kit(param.kitId(), kit.getTitle()),
            new KitUserPaginatedResponse.ExpertGroup(expertGroup.getId(), expertGroup.getName())
        );
    }
}
