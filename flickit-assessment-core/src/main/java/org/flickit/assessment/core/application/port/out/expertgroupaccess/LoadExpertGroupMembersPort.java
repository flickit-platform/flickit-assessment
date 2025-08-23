package org.flickit.assessment.core.application.port.out.expertgroupaccess;

import java.util.List;
import java.util.UUID;

public interface LoadExpertGroupMembersPort {

    List<Member> loadExpertGroupMembers(long expertGroupId);

    record Member(UUID id, String email, String displayName) {}
}
