package org.flickit.assessment.users.adapter.out.persistence.spaceaccess;

import org.flickit.assessment.users.application.port.out.spaceaccess.*;

import java.time.LocalDateTime;
import java.util.UUID;

public class SpaceAccessJpaAdapter implements
AddSpaceMemberPort,
    CheckMemberSpaceAccessPort
{
    @Override
    public void addMemberAccess(Param param) {

    }

    @Override
    public Boolean checkAccess(UUID userId) {
        return null;
    }
}
