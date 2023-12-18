package org.flickit.assessment.kit.adapter.out.persistence.expertgroup;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.data.jpa.kit.assessmentkit.AssessmentKitJpaEntity;
import org.flickit.assessment.data.jpa.kit.assessmentkit.AssessmentKitJpaRepository;
import org.flickit.assessment.data.jpa.kit.expertgroup.ExpertGroupJpaEntity;
import org.flickit.assessment.data.jpa.kit.expertgroup.ExpertGroupJpaRepository;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static org.flickit.assessment.kit.common.ErrorMessageKey.GRANT_USER_ACCESS_TO_KIT_EXPERT_GROUP_OWNER_NOT_FOUND;
import static org.flickit.assessment.kit.common.ErrorMessageKey.GRANT_USER_ACCESS_TO_KIT_KIT_ID_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class ExpertGroupPersistenceJpaAdapterId implements
    LoadExpertGroupOwnerPort {

    private final ExpertGroupJpaRepository repository;
    private final AssessmentKitJpaRepository kitRepository;

    @Override
    public UUID loadByKitId(Long kitId) {
        AssessmentKitJpaEntity assessmentKit = kitRepository.findById(kitId)
            .orElseThrow(() -> new ResourceNotFoundException(GRANT_USER_ACCESS_TO_KIT_KIT_ID_NOT_FOUND));

        ExpertGroupJpaEntity expertGroup = repository.findById(assessmentKit.getExpertGroupId())
            .orElseThrow(() -> new ResourceNotFoundException(GRANT_USER_ACCESS_TO_KIT_EXPERT_GROUP_OWNER_NOT_FOUND));
        return expertGroup.getOwnerId();
    }
}
