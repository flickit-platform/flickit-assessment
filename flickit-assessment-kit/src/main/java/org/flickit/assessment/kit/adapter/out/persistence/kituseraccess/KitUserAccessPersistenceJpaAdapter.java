package org.flickit.assessment.kit.adapter.out.persistence.kituseraccess;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.data.jpa.kit.kituseraccess.KitUserAccessJpaEntity;
import org.flickit.assessment.data.jpa.kit.kituseraccess.KitUserAccessJpaRepository;
import org.flickit.assessment.data.jpa.kit.user.UserJpaEntity;
import org.flickit.assessment.data.jpa.kit.user.UserJpaRepository;
import org.flickit.assessment.kit.application.domain.KitUser;
import org.flickit.assessment.kit.application.port.out.kituseraccess.LoadKitUserAccessPort;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static org.flickit.assessment.kit.common.ErrorMessageKey.GRANT_USER_ACCESS_TO_KIT_EMAIL_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class KitUserAccessPersistenceJpaAdapter implements LoadKitUserAccessPort {

    private final KitUserAccessJpaRepository repository;
    private final UserJpaRepository userRepository;

    @Override
    public Optional<KitUser> loadByKitIdAndUserEmail(Long kitId, String email) {
        UserJpaEntity user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException(GRANT_USER_ACCESS_TO_KIT_EMAIL_NOT_FOUND));
        return repository.findById(new KitUserAccessJpaEntity.KitUserAccessKey(kitId, user.getId())).map(KitUserAccessMapper::mapToDomainModel);
    }
}
