package org.flickit.assessment.kit.adapter.out.persistence.assessmentkit;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.data.jpa.kit.assessmentkit.AssessmentKitJpaEntity;
import org.flickit.assessment.data.jpa.kit.assessmentkit.AssessmentKitJpaRepository;
import org.flickit.assessment.data.jpa.kit.user.UserJpaEntity;
import org.flickit.assessment.data.jpa.kit.user.UserJpaRepository;
import org.flickit.assessment.kit.adapter.out.persistence.user.UserMapper;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.User;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadAssessmentKitOwnerPort;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadKitByIdPort;
import org.flickit.assessment.kit.application.port.out.useraccess.GrantUserAccessToKitPort;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static org.flickit.assessment.kit.common.ErrorMessageKey.*;

@Component
@RequiredArgsConstructor
public class AssessmentKitPersistenceJpaAdapter implements
    GrantUserAccessToKitPort,
    LoadAssessmentKitOwnerPort,
    LoadKitByIdPort {

    private final AssessmentKitJpaRepository repository;
    private final UserJpaRepository userRepository;

    @Override
    public boolean grantUserAccessToKitByUserEmail(Long kitId, String userEmail) {
        AssessmentKitJpaEntity assessmentKit = repository.findById(kitId)
            .orElseThrow(() -> new ResourceNotFoundException(GRANT_USER_ACCESS_TO_KIT_KIT_ID_NOT_FOUND));
        UserJpaEntity user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new ResourceNotFoundException(GRANT_USER_ACCESS_TO_KIT_USER_EMAIL_NOT_FOUND));

        boolean isAccessUpdated = assessmentKit.getAccessGrantedUsers().add(user);
        repository.save(assessmentKit);

        return isAccessUpdated;
    }

    @Override
    public User loadKitOwnerById(Long kitId) {
        AssessmentKitJpaEntity assessmentKit = repository.findById(kitId)
            .orElseThrow(() -> new ResourceNotFoundException(GRANT_USER_ACCESS_TO_KIT_KIT_ID_NOT_FOUND));
        if (assessmentKit.getExpertGroup() != null && assessmentKit.getExpertGroup().getOwner() != null) {
            UserJpaEntity kitOwner = assessmentKit.getExpertGroup().getOwner();
            return UserMapper.mapToDomainModel(kitOwner);
        } else
            throw new ResourceNotFoundException(GRANT_USER_ACCESS_TO_KIT_KIT_OWNER_NOT_FOUND);
    }

    @Override
    public Optional<AssessmentKit> load(Long kitId) {
        var entity = repository.findById(kitId);
        return entity.map(AssessmentKitMapper::mapToDomainModel);
    }
}
