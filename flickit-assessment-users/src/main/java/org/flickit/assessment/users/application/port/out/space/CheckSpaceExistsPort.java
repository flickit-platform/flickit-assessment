package org.flickit.assessment.users.application.port.out.space;

public interface CheckSpaceExistsPort {

    boolean existById(long spaceId); //TODO: Change to existByIdDeletedFalse after merging Delete Space
}
