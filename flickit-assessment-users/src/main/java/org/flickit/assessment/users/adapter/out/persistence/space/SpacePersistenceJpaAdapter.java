package org.flickit.assessment.users.adapter.out.persistence.space;

//import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.users.application.port.out.space.CheckSpaceExistencePort;

//import static org.flickit.assessment.users.common.ErrorMessageKey.ADD_SPACE_MEMBER_SPACE_ID_NOT_FOUND;

public class SpacePersistenceJpaAdapter implements
    CheckSpaceExistencePort {

    @Override
    public boolean existsById(long id) {
        //var isExist = repository.existsByIdAndDeletedFalse(id)
           // .orElseThrow(() -> new ResourceNotFoundException(ADD_SPACE_MEMBER_SPACE_ID_NOT_FOUND));
        return true;
    }
}
