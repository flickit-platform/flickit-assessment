package org.flickit.assessment.kit.application.port.out.expertgroup;

import java.util.List;
import java.util.UUID;

public interface LoadExpertGroupMemberIdsPort {

    List<Result> loadMemberIds(long expertGroupId);

    record Result(UUID userId){
    }
}
