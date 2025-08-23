package org.flickit.assessment.core.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.port.out.expertgroupaccess.LoadExpertGroupMembersPort;
import org.flickit.assessment.data.jpa.users.expertgroupaccess.ExpertGroupAccessJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("coreExpertGroupAccessPersistenceJpaAdapter")
@RequiredArgsConstructor
public class ExpertGroupAccessPersistenceJpaAdapter implements LoadExpertGroupMembersPort {

    private final ExpertGroupAccessJpaRepository repository;

    @Override
    public List<Member> loadExpertGroupMembers(long expertGroupId) {
        var activeMembers = repository.findActiveMembers(expertGroupId);
        return activeMembers.stream()
            .map(e -> new Member(e.getId(), e.getEmail(), e.getDisplayName()))
            .toList();
    }
}
