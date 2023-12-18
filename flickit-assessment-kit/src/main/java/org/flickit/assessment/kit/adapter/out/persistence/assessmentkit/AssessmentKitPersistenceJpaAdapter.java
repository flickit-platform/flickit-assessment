package org.flickit.assessment.kit.adapter.out.persistence.assessmentkit;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.data.jpa.kit.assessmentkit.AssessmentKitJpaEntity;
import org.flickit.assessment.data.jpa.kit.assessmentkit.AssessmentKitJpaRepository;
import org.flickit.assessment.data.jpa.kit.user.UserJpaEntity;
import org.flickit.assessment.data.jpa.kit.user.UserJpaRepository;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadExpertGroupIdPort;
import org.flickit.assessment.kit.application.port.out.useraccess.GrantUserAccessToKitPort;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static org.flickit.assessment.kit.common.ErrorMessageKey.GRANT_USER_ACCESS_TO_KIT_EMAIL_NOT_FOUND;
import static org.flickit.assessment.kit.common.ErrorMessageKey.GRANT_USER_ACCESS_TO_KIT_KIT_ID_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class AssessmentKitPersistenceJpaAdapter implements
    GrantUserAccessToKitPort,
    LoadExpertGroupIdPort {

    private final AssessmentKitJpaRepository repository;
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
}
