package org.flickit.assessment.users.application.port.out.spaceaccess;

public interface CheckSpaceExistencePort {

    boolean existsById(long spaceId);
}
