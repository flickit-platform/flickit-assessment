package org.flickit.assessment.kit.adapter.out.persistence.user;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.kit.user.UserJpaEntity;
import org.flickit.assessment.data.jpa.kit.user.UserJpaRepository;
import org.flickit.assessment.kit.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.kit.application.port.in.user.GetUserListUseCase;
import org.flickit.assessment.kit.application.port.out.user.LoadUsersByKitPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserPersistenceJpaAdapter implements LoadUsersByKitPort {

    private final UserJpaRepository repository;

    @Override
    public PaginatedResponse<GetUserListUseCase.UserListItem> load(LoadUsersByKitPort.Param param) {
        Page<UserJpaEntity> pageResult = repository.findAllByAccessToKitId(
            param.kitId(),
            PageRequest.of(param.page(), param.size()));

        var items = pageResult.getContent().stream()
            .map(UserMapper::mapJpaEntityToUserItem)
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
