package org.flickit.assessment.core.adapter.out.persistence.assessmentinvitee;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.core.application.domain.AssessmentInvitee;
import org.flickit.assessment.core.application.port.out.assessmentinvitee.LoadAssessmentInviteeListPort;
import org.flickit.assessment.data.jpa.core.assessmentinvitee.AssessmentInviteeJpaEntity;
import org.flickit.assessment.data.jpa.core.assessmentinvitee.AssessmentInviteeJpaRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AssessmentInviteePersistenceJpaAdapter implements
    LoadAssessmentInviteeListPort {

    private final AssessmentInviteeJpaRepository repository;

    @Override
    public PaginatedResponse<AssessmentInvitee> loadByAssessmentId(UUID assessmentId, int size, int page) {
        var order = AssessmentInviteeJpaEntity.Fields.creationTime;
        var sort = Sort.Direction.DESC;

        var pageResult = repository.findByAssessmentId(assessmentId,
            PageRequest.of(page, size, sort, order));

        var items = pageResult.getContent().stream()
            .map(AssessmentInviteeMapper::mapToDomainModel)
            .toList();

        return new PaginatedResponse<>(
            items,
            pageResult.getNumber(),
            pageResult.getSize(),
            order,
            sort.name().toLowerCase(),
            (int) pageResult.getTotalElements());
    }
}
