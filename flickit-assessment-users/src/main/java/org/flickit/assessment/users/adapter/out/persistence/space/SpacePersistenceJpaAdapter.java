package org.flickit.assessment.users.adapter.out.persistence.space;

import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.users.application.domain.Space;
import org.flickit.assessment.users.application.port.out.space.LoadSpacePort;

import static org.flickit.assessment.users.adapter.out.persistence.space.SpaceMapper.mapToDomainModel;
import static org.flickit.assessment.users.common.ErrorMessageKey.ADD_SPACE_MEMBER_SPACE_ID_NOT_FOUND;

public class SpacePersistenceJpaAdapter implements
    LoadSpacePort {

    @Override
    public Space loadSpace(long id) {
        var resultEntity = repository.findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new ResourceNotFoundException(ADD_SPACE_MEMBER_SPACE_ID_NOT_FOUND));
        return mapToDomainModel(resultEntity);
    }
}
