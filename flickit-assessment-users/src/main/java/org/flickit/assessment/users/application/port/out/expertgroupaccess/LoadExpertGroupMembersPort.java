package org.flickit.assessment.users.application.port.out.expertgroupaccess;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;

import java.time.LocalDateTime;
import java.util.UUID;

public interface LoadExpertGroupMembersPort {

    PaginatedResponse<Member> loadExpertGroupMembers(long expertGroupId, int status, int page, int size);

    record Member(UUID id, String email, String displayName, String bio, String picture,
                  String linkedin, int status, LocalDateTime inviteExpirationDate) {
    }
}
