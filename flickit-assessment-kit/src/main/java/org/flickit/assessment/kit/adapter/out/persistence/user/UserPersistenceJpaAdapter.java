package org.flickit.assessment.kit.adapter.out.persistence.user;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.data.jpa.kit.assessmentkit.AssessmentKitJpaRepository;
import org.flickit.assessment.data.jpa.kit.expertgroup.ExpertGroupJpaRepository;
import org.flickit.assessment.data.jpa.kit.user.UserJpaEntity;
import org.flickit.assessment.data.jpa.kit.user.UserJpaRepository;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitUserListUseCase;
import org.flickit.assessment.kit.application.port.out.user.LoadUsersByKitPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_KIT_USER_LIST_EXPERT_GROUP_NOT_FOUND;
import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_KIT_USER_LIST_KIT_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class UserPersistenceJpaAdapter implements LoadUsersByKitPort {

    private final UserJpaRepository repository;
    private final AssessmentKitJpaRepository kitRepository;
    private final ExpertGroupJpaRepository expertGroupRepository;

    @Override
    public PaginatedResponse<GetKitUserListUseCase.KitUserListItem> load(LoadUsersByKitPort.Param param) {
        var kit = kitRepository.findById(param.kitId()).orElseThrow(
            () -> new ResourceNotFoundException(GET_KIT_USER_LIST_KIT_NOT_FOUND));
        var expertGroup = expertGroupRepository.findById(kit.getExpertGroupId()).orElseThrow(
            () -> new ResourceNotFoundException(GET_KIT_USER_LIST_EXPERT_GROUP_NOT_FOUND));

        Page<UserJpaEntity> pageResult = repository.findAllKitUsers(
            param.kitId(),
            PageRequest.of(param.page(), param.size()));

        var items = pageResult.getContent().stream()
            .map(u -> UserMapper.mapJpaEntityToUserItem(u, kit.getTitle(), expertGroup.getName()))
            .toList();

        return new PaginatedResponse<>(
            items,
            pageResult.getNumber(),
            pageResult.getSize(),
            UserJpaEntity.Fields.ID,
            Sort.Direction.ASC.name().toLowerCase(),
            (int) pageResult.getTotalElements()
        );
    }
}
