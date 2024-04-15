package org.flickit.assessment.users.application.port.out.space;

public interface CheckSpaceExistencePort {

    boolean existsById(long spaceId);
}
