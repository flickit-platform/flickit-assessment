package org.flickit.assessment.users.application.port.out.expertgroupaccess;

import org.flickit.assessment.common.exception.ResourceNotFoundException;

import java.util.UUID;

public interface DeleteExpertGroupMemberPort {

    /**
     * Deletes a member from the specified expert group.
     * @param expertGroupId The ID of the expert group from which the member will be removed.
     * @param userId The ID of the user to be removed from the expert group.
     * @throws ResourceNotFoundException if the user does not already have access to the expert group.
     */
    void deleteMember (long expertGroupId, UUID userId);
}
