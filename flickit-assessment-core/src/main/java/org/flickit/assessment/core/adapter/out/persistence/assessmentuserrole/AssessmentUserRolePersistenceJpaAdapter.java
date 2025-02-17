package org.flickit.assessment.core.adapter.out.persistence.assessmentuserrole;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.ResourceAlreadyExistsException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AssessmentUserRole;
import org.flickit.assessment.core.application.domain.AssessmentUserRoleItem;
import org.flickit.assessment.core.application.domain.FullUser;
import org.flickit.assessment.core.application.port.out.assessmentuserrole.*;
import org.flickit.assessment.data.jpa.core.assessmentuserrole.AssessmentUserRoleJpaEntity;
import org.flickit.assessment.data.jpa.core.assessmentuserrole.AssessmentUserRoleJpaRepository;
import org.flickit.assessment.data.jpa.core.assessmentuserrole.AssessmentUserView;
import org.flickit.assessment.data.jpa.users.user.UserJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static org.flickit.assessment.core.common.ErrorMessageKey.*;

@Component
@RequiredArgsConstructor
public class AssessmentUserRolePersistenceJpaAdapter implements
    LoadUserRoleForAssessmentPort,
    GrantUserAssessmentRolePort,
    UpdateUserAssessmentRolePort,
    DeleteUserAssessmentRolePort,
    LoadAssessmentUsersPort {

    private final AssessmentUserRoleJpaRepository repository;

    @Override
    public Optional<AssessmentUserRole> load(UUID assessmentId, UUID userId) {
        return repository.findByAssessmentIdAndUserId(assessmentId, userId)
            .map(result -> AssessmentUserRole.valueOfById(result.getRoleId()));
    }

    @Override
    public void persist(UUID assessmentId, UUID userId, Integer roleId) {
        if (!AssessmentUserRole.isValidId(roleId))
            throw new ResourceNotFoundException(GRANT_ASSESSMENT_USER_ROLE_ROLE_ID_NOT_FOUND);

        var assessmentUserRole = repository.findByAssessmentIdAndUserId(assessmentId, userId);
        if(assessmentUserRole.isPresent())
            throw new ResourceAlreadyExistsException(GRANT_ASSESSMENT_USER_ROLE_DUPLICATE_USER_ACCESS);

        var entity = new AssessmentUserRoleJpaEntity(assessmentId, userId, roleId);
        repository.save(entity);
    }

    @Override
    public void persistAll(List<AssessmentUserRoleItem> assementUserRoleItemList) {
        var assessmentUserRoles = assementUserRoleItemList.stream()
            .map(AssessmentUserRoleMapper::mapToJpEntity)
            .toList();
        repository.saveAll(assessmentUserRoles);
    }

    @Override
    public void update(UUID assessmentId, UUID userId, Integer roleId) {
        if (!AssessmentUserRole.isValidId(roleId))
            throw new ResourceNotFoundException(UPDATE_ASSESSMENT_USER_ROLE_ROLE_ID_NOT_FOUND);

        if (!repository.existsByAssessmentIdAndUserId(assessmentId, userId))
            throw new ResourceNotFoundException(UPDATE_ASSESSMENT_USER_ROLE_ASSESSMENT_ID_USER_ID_NOT_FOUND);

        var entity = new AssessmentUserRoleJpaEntity(assessmentId, userId, roleId);
        repository.update(entity.getAssessmentId(), entity.getUserId(), entity.getRoleId());
    }

    @Override
    public void delete(UUID assessmentId, UUID userId) {
        if (!repository.existsByAssessmentIdAndUserId(assessmentId, userId))
            throw new ResourceNotFoundException(DELETE_ASSESSMENT_USER_ROLE_ASSESSMENT_ID_USER_ID_NOT_FOUND);

        repository.deleteByAssessmentIdAndUserId(assessmentId, userId);
    }

    @Override
    public PaginatedResponse<AssessmentUser> loadAssessmentUsers(Param param) {
        Page<AssessmentUserView> pageResult = repository.findAssessmentUsers(param.assessmentId(),
            PageRequest.of(param.page(), param.size(), Sort.Direction.ASC, UserJpaEntity.Fields.displayName));

        List<AssessmentUser> assessmentUsers = pageResult.getContent().stream()
            .map(e -> {
                AssessmentUserRole assessmentUserRole = AssessmentUserRole.valueOfById(e.getRoleId());
                if (assessmentUserRole == null)
                    return null;
                else {
                    return new AssessmentUser(e.getUserId(),
                        e.getEmail(),
                        e.getDisplayName(),
                        e.getPicturePath(),
                        new AssessmentUser.Role(e.getRoleId(), assessmentUserRole.getTitle()),
                        e.getEditable());
                }
            }) .filter(Objects::nonNull)
            .toList();

        return new PaginatedResponse<>(
            assessmentUsers,
            pageResult.getNumber(),
            pageResult.getSize(),
            UserJpaEntity.Fields.displayName,
            Sort.Direction.ASC.name().toLowerCase(),
            (int) pageResult.getTotalElements()
        );
    }

    @Override
    public List<FullUser> loadAll(UUID assessmentId, List<Integer> roleIds) {
        return repository.findUsersByRoles(assessmentId, roleIds).stream()
            .map(e -> new FullUser(e.getUserId(), e.getDisplayName(), e.getEmail(), e.getPicturePath()))
            .toList();
    }
}
