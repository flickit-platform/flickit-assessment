package org.flickit.assessment.core.adapter.out.persistence.assessmentinvite;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AssessmentInvite;
import org.flickit.assessment.core.application.domain.AssessmentUserRole;
import org.flickit.assessment.core.application.port.out.assessmentinvite.*;
import org.flickit.assessment.data.jpa.core.assessmentinvitee.AssessmentInviteeJpaEntity;
import org.flickit.assessment.data.jpa.core.assessmentinvitee.AssessmentInviteeJpaRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.core.adapter.out.persistence.assessmentinvite.AssessmentInviteMapper.mapToJpaEntity;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;

@Component
@RequiredArgsConstructor
public class AssessmentInvitePersistenceJpaAdapter implements
    LoadAssessmentInviteeListPort,
    LoadAssessmentsUserInvitationsPort,
    CreateAssessmentInvitePort,
    DeleteAssessmentUserInvitationPort,
    LoadAssessmentInvitePort,
    DeleteAssessmentInvitePort,
    UpdateAssessmentInvitePort {

    private final AssessmentInviteeJpaRepository repository;

    @Override
    public PaginatedResponse<AssessmentInvite> loadByAssessmentId(UUID assessmentId, int size, int page) {
        var order = AssessmentInviteeJpaEntity.Fields.creationTime;
        var sort = Sort.Direction.DESC;

        var pageResult = repository.findByAssessmentId(assessmentId,
            PageRequest.of(page, size, sort, order));

        var items = pageResult.getContent().stream()
            .map(AssessmentInviteMapper::mapToDomainModel)
            .toList();

        return new PaginatedResponse<>(
            items,
            pageResult.getNumber(),
            pageResult.getSize(),
            order,
            sort.name().toLowerCase(),
            (int) pageResult.getTotalElements());
    }

    @Override
    public List<AssessmentInvite> loadInvitations(String email) {
        return repository.findAllByEmail(email).stream()
            .map(AssessmentInviteMapper::mapToDomainModel)
            .toList();
    }

    @Override
    public void persist(CreateAssessmentInvitePort.Param param) {
        if (!AssessmentUserRole.isValidId(param.roleId()))
            throw new ResourceNotFoundException(INVITE_ASSESSMENT_USER_ROLE_ID_NOT_FOUND);

        var invitation = repository.findByAssessmentIdAndEmail(param.assessmentId(), param.email());

        AssessmentInviteeJpaEntity entity;
        entity = invitation.map(assessmentInviteeJpaEntity -> mapToJpaEntity(invitation.get().getId(), param))
            .orElseGet(() -> mapToJpaEntity(null, param));

        repository.save(entity);
    }

    @Override
    public void deleteAllByEmail(String email) {
        repository.deleteByEmail(email);
    }

    @Override
    public AssessmentInvite load(UUID id) {
        return repository.findById(id)
            .map(AssessmentInviteMapper::mapToDomainModel)
            .orElseThrow(() -> new ResourceNotFoundException(ASSESSMENT_INVITE_ID_NOT_FOUND));
    }

    @Override
    public void delete(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public void updateRole(UUID id, int roleId) {
        if (!AssessmentUserRole.isValidId(roleId))
            throw new ResourceNotFoundException(UPDATE_ASSESSMENT_INVITE_ROLE_ID_NOT_FOUND);

        repository.updateRoleById(id, roleId);
    }
}
