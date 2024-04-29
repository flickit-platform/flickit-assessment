package org.flickit.assessment.users.adapter.out.persistence.space;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.data.jpa.users.space.SpaceJpaRepository;
import org.flickit.assessment.users.application.port.out.space.CheckSpaceExistsPort;
import org.flickit.assessment.users.application.port.out.space.CountSpaceAssessmentPort;
import org.flickit.assessment.users.application.port.out.space.DeleteSpacePort;
import org.flickit.assessment.users.application.port.out.space.LoadSpaceOwnerPort;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static org.flickit.assessment.users.common.ErrorMessageKey.SPACE_ID_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class SpacePersistenceJpaAdapter implements
    LoadSpaceOwnerPort,
    CheckSpaceExistsPort,
    CountSpaceAssessmentPort,
    DeleteSpacePort {

    private final SpaceJpaRepository repository;

    @Override
    public UUID loadOwnerId(long id) {
        return repository.loadOwnerIdById(id)
            .orElseThrow(() -> new ResourceNotFoundException(SPACE_ID_NOT_FOUND));
    }

    @Override
    public boolean existsById(long id) {
        return repository.existsByIdAndDeletedFalse(id);
    }

    @Override
    public int countAssessments(long spaceId) {
        return repository.countAssessments(spaceId);
    }

    @Override
    public void deleteById(long spaceId, long deletionTime) {
        repository.delete(spaceId, deletionTime);
    }
}
