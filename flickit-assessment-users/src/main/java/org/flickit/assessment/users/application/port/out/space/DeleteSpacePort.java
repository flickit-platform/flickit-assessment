package org.flickit.assessment.users.application.port.out.space;

public interface DeleteSpacePort {

    void deleteById(long spaceId, long deletionTime);
}
