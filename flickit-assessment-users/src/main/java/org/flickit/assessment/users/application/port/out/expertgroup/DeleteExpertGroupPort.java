package org.flickit.assessment.users.application.port.out.expertgroup;

public interface DeleteExpertGroupPort {

    void deleteById(long expertGroupId, long deletionTime);
}
