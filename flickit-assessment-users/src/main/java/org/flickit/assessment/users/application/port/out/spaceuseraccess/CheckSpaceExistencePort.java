package org.flickit.assessment.users.application.port.out.spaceuseraccess;

public interface CheckSpaceExistencePort {

    boolean existsById(long spaceId);
}
