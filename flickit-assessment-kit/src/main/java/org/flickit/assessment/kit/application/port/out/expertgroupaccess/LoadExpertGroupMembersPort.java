package org.flickit.assessment.kit.application.port.out.expertgroupaccess;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;

import java.util.UUID;

public interface LoadExpertGroupMembersPort {

    PaginatedResponse<Member> loadExpertGroupMembers(long expertGroupId, int page, int size);

    record Member(UUID id, String email, String displayName, String bio, String picture, String linkedin) {
    }
}
