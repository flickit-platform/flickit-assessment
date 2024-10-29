package org.flickit.assessment.core.application.port.out.expertgroup;


import java.util.List;
import java.util.UUID;

public interface LoadExpertGroupMembersPort {

    List<Member> loadExpertGroupMembers(long expertGroupId, int status);

    record Member(UUID id, String email, String displayName) {}
}
