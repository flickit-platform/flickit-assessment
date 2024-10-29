package org.flickit.assessment.core.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.port.out.expertgroup.LoadExpertGroupMembersPort;
import org.flickit.assessment.data.jpa.users.expertgroupaccess.ExpertGroupAccessJpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component("coreExpertGroupAccessPersistenceJpaAdapter")
@RequiredArgsConstructor
public class ExpertGroupAccessPersistenceJpaAdapter implements LoadExpertGroupMembersPort {

    private final ExpertGroupAccessJpaRepository repository;

    @Override
    public List<Member> loadExpertGroupMembers(long expertGroupId, int status) {
        var expertGroupMembers = repository.findExpertGroupMembers(expertGroupId,
            status,
            LocalDateTime.now(),
            Pageable.unpaged());
        return expertGroupMembers.getContent().stream()
            .map(e -> new Member(e.getId(), e.getEmail(), e.getDisplayName()))
            .toList();
    }
}
