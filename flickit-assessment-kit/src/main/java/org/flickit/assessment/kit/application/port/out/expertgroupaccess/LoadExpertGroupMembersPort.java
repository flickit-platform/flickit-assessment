package org.flickit.assessment.kit.application.port.out.expertgroupaccess;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;

import java.util.UUID;

public interface LoadExpertGroupMembersPort {

    PaginatedResponse<Result> loadExpertGroupMembers(Param param);

    record Result(UUID id, String email, String displayNme, String bio, String picture, String linkedin) {
    }

    record Param(int page, int size, long expertGroupId) {
    }
}
