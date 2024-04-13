package org.flickit.assessment.users.application.service.space;

import org.flickit.assessment.users.application.port.in.space.AddSpaceMemberUseCase;

import java.util.UUID;

public class AddSpaceMemberService implements AddSpaceMemberUseCase {

    @Override
    public void addMember(long spaceId, String email, UUID currentUserId) {
    }
}
